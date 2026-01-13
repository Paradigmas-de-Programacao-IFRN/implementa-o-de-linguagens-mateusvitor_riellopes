package plp.enquanto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

interface Linguagem {
	Map<String, Integer> ambiente = new HashMap<>();
	Scanner scanner = new Scanner(System.in);

	interface Bool {
		boolean getValor();
	}

	interface Comando {
		void execute();
	}

	interface Expressao {
		int getValor();
	}

	/*
	  Comandos
	 */
	class Programa {
		private final List<Comando> comandos;
		public Programa(List<Comando> comandos) {
			this.comandos = comandos;
		}
		public void execute() {
			comandos.forEach(Comando::execute);
		}
	}

	class Se implements Comando {
		private final Bool condicao;
		private final Comando entao;
		private final List<SenaoSe> senaoSes;
		private final Comando senao;

		public Se(Bool condicao, Comando entao, List<SenaoSe> senaoSes, Comando senao) {
			this.condicao = condicao;
			this.entao = entao;
			this.senaoSes = senaoSes;
			this.senao = senao;
		}

		@Override
		public void execute() {
			if (condicao.getValor()) {
				entao.execute();
				return;
			}
			for (SenaoSe s : senaoSes) {
				if (s.condicao.getValor()) {
					s.comando.execute();
					return;
				}
			}
			senao.execute();
		}
	}

	class SenaoSe {
		private final Bool condicao;
		private final Comando comando;

		SenaoSe(Bool condicao, Comando comando) {
			this.condicao = condicao;
			this.comando = comando;
		}
	}

	Skip skip = new Skip();
	class Skip implements Comando {
		@Override
		public void execute() {}
	}

	class Escreva implements Comando {
		private final Expressao exp;

		public Escreva(Expressao exp) {
			this.exp = exp;
		}

		@Override
		public void execute() {
			System.out.println(exp.getValor());
		}
	}

	class Enquanto implements Comando {
		private final Bool condicao;
		private final Comando comando;

		public Enquanto(Bool condicao, Comando comando) {
			this.condicao = condicao;
			this.comando = comando;
		}

		@Override
		public void execute() {
			while (condicao.getValor()) {
				comando.execute();
			}
		}
	}

	class Exiba implements Comando {
		private final String texto;
		private final Expressao expressao;

		public Exiba(String texto) {
			this.texto = texto;
			this.expressao = null;
		}

		public Exiba(Expressao expressao) {
			this.texto = null;
			this.expressao = expressao;
		}

		@Override
		public void execute() {
			if (texto != null) {
				System.out.println(texto);
			} else {
				System.out.println(expressao.getValor());
			}
		}
	}

	class Bloco implements Comando {
		private final List<Comando> comandos;

		public Bloco(List<Comando> comandos) {
			this.comandos = comandos;
		}

		@Override
		public void execute() {
			comandos.forEach(Comando::execute);
		}
	}

	class Atribuicao implements Comando {
		private final String id;
		private final Expressao exp;

		Atribuicao(String id, Expressao exp) {
			this.id = id;
			this.exp = exp;
		}

		@Override
		public void execute() {
			ambiente.put(id, exp.getValor());
		}
	}

	class AtribuicaoMultipla implements Comando {
		private final List<String> ids;
		private final List<Expressao> exps;

		AtribuicaoMultipla(List<String> ids, List<Expressao> exps) {
			this.ids = ids;
			this.exps = exps;
		}

		@Override
		public void execute() {
			List<Integer> valores = new ArrayList<>();
			for (Expressao e : exps) {
				valores.add(e.getValor());
			}
			for (int i = 0; i < ids.size(); i++) {
				int valor = i < valores.size() ? valores.get(i) : 0;
				ambiente.put(ids.get(i), valor);
			}
		}
	}

	class Para implements Comando {
		private final String id;
		private final Expressao inicio;
		private final Expressao fim;
		private final Expressao passo;
		private final Comando comando;

		Para(String id, Expressao inicio, Expressao fim, Expressao passo, Comando comando) {
			this.id = id;
			this.inicio = inicio;
			this.fim = fim;
			this.passo = passo;
			this.comando = comando;
		}

		@Override
		public void execute() {
			int i = inicio.getValor();
			while (true) {
				int passoVal = passo == null ? 1 : passo.getValor();
				if (passoVal == 0) passoVal = 1;
				int limite = fim.getValor();
				if ((passoVal > 0 && i > limite) || (passoVal < 0 && i < limite)) break;
				ambiente.put(id, i);
				comando.execute();
				i += passoVal;
			}
		}
	}

	class Repita implements Comando {
		private final Expressao vezes;
		private final Comando comando;

		Repita(Expressao vezes, Comando comando) {
			this.vezes = vezes;
			this.comando = comando;
		}

		@Override
		public void execute() {
			int v = vezes.getValor();
			for (int i = 0; i < v; i++) {
				comando.execute();
			}
		}
	}

	class Escolha implements Comando {
		private final Expressao expressao;
		private final List<Caso> casos;
		private final Comando outro;

		Escolha(Expressao expressao, List<Caso> casos, Comando outro) {
			this.expressao = expressao;
			this.casos = casos;
			this.outro = outro;
		}

		@Override
		public void execute() {
			int chave = expressao.getValor();
			for (Caso c : casos) {
				if (c.valor.getValor() == chave) {
					c.comando.execute();
					return;
				}
			}
			if (outro != null) outro.execute();
		}
	}

	class Caso {
		private final Expressao valor;
		private final Comando comando;

		Caso(Expressao valor, Comando comando) {
			this.valor = valor;
			this.comando = comando;
		}
	}

	/*
	   Expressoes
	 */

	abstract class OpBin<T>  {
		protected final T esq;
		protected final T dir;

		OpBin(T esq, T dir) {
			this.esq = esq;
			this.dir = dir;
		}
	}

	abstract class OpUnaria<T>  {
		protected final T operando;

		OpUnaria(T operando) {
			this.operando = operando;
		}
	}

	class Inteiro implements Expressao {
		private final int valor;

		Inteiro(int valor) {
			this.valor = valor;
		}

		@Override
		public int getValor() {
			return valor;
		}
	}

	class Id implements Expressao {
		private final String id;

		Id(String id) {
			this.id = id;
		}

		@Override
		public int getValor() {
			return ambiente.getOrDefault(id, 0);
		}
	}

	Leia leia = new Leia();
	class Leia implements Expressao {
		@Override
		public int getValor() {
			return scanner.nextInt();
		}
	}

	class ExpSoma extends OpBin<Expressao> implements Expressao {
		ExpSoma(Expressao esq, Expressao dir) {
			super(esq, dir);
		}

		@Override
		public int getValor() {
			return esq.getValor() + dir.getValor();
		}
	}

	class ExpSub extends OpBin<Expressao> implements Expressao {
		ExpSub(Expressao esq, Expressao dir) {
			super(esq, dir);
		}

		@Override
		public int getValor() {
			return esq.getValor() - dir.getValor();
		}
	}

	class ExpMult extends OpBin<Expressao> implements Expressao{
		ExpMult(Expressao esq, Expressao dir) {
			super(esq, dir);
		}

		@Override
		public int getValor() {
			return esq.getValor() * dir.getValor();
		}
	}

	class ExpDiv extends OpBin<Expressao> implements Expressao{
		ExpDiv(Expressao esq, Expressao dir) {
			super(esq, dir);
		}

		@Override
		public int getValor() {
			return esq.getValor() / dir.getValor();
		}
	}

	class ExpPot extends OpBin<Expressao> implements Expressao{
		ExpPot(Expressao esq, Expressao dir) {
			super(esq, dir);
		}

		@Override
		public int getValor() {
			return (int) Math.pow(esq.getValor(), dir.getValor());
		}
	}

	class Booleano implements Bool {
		private final boolean valor;

		Booleano(boolean valor) {
			this.valor = valor;
		}

		@Override
		public boolean getValor() {
			return valor;
		}
	}

	class ExpIgual extends OpBin<Expressao> implements Bool {
		ExpIgual(Expressao esq, Expressao dir) {
			super(esq, dir);
		}

		@Override
		public boolean getValor() {
			return esq.getValor() == dir.getValor();
		}
	}

	class ExpMenorIgual extends OpBin<Expressao> implements Bool{
		ExpMenorIgual(Expressao esq, Expressao dir) {
			super(esq, dir);
		}

		@Override
		public boolean getValor() {
			return esq.getValor() <= dir.getValor();
		}
	}

	class ExpMenor extends OpBin<Expressao> implements Bool{
		ExpMenor(Expressao esq, Expressao dir) { super(esq, dir); }
		@Override public boolean getValor() { return esq.getValor() < dir.getValor(); }
	}

	class ExpMaior extends OpBin<Expressao> implements Bool{
		ExpMaior(Expressao esq, Expressao dir) { super(esq, dir); }
		@Override public boolean getValor() { return esq.getValor() > dir.getValor(); }
	}

	class ExpMaiorIgual extends OpBin<Expressao> implements Bool{
		ExpMaiorIgual(Expressao esq, Expressao dir) { super(esq, dir); }
		@Override public boolean getValor() { return esq.getValor() >= dir.getValor(); }
	}

	class ExpDiferente extends OpBin<Expressao> implements Bool{
		ExpDiferente(Expressao esq, Expressao dir) { super(esq, dir); }
		@Override public boolean getValor() { return esq.getValor() != dir.getValor(); }
	}

	class NaoLogico extends OpUnaria<Bool> implements Bool{
		NaoLogico(Bool operando) {
			super(operando);
		}

		@Override
		public boolean getValor() {
			return !operando.getValor();
		}
	}

	class ELogico extends OpBin<Bool> implements Bool{
		ELogico(Bool esq, Bool dir) {
			super(esq, dir);
		}

		@Override
		public boolean getValor() {
			return esq.getValor() && dir.getValor();
		}
	}

	class OuLogico extends OpBin<Bool> implements Bool{
		OuLogico(Bool esq, Bool dir) { super(esq, dir); }
		@Override public boolean getValor() { return esq.getValor() || dir.getValor(); }
	}

	class XorLogico extends OpBin<Bool> implements Bool{
		XorLogico(Bool esq, Bool dir) { super(esq, dir); }
		@Override public boolean getValor() { return esq.getValor() ^ dir.getValor(); }
	}
}
