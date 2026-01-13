package plp.enquanto;

import java.util.ArrayList;
import java.util.List;

import plp.enquanto.Linguagem.*;
import plp.enquanto.parser.EnquantoBaseListener;
import plp.enquanto.parser.EnquantoParser.*;

import static java.lang.Integer.parseInt;

public class Regras extends EnquantoBaseListener {
	private final Leia leia;
	private final Skip skip;
	private final Propriedades valores;

	private Programa programa;

	public Regras() {
		leia = new Leia();
		skip = new Skip();
		valores = new Propriedades();
	}

	public Programa getPrograma() {
		return programa;
	}

	@Override
	public void exitBool(BoolContext ctx) {
		valores.insira(ctx, new Booleano("verdadeiro".equals(ctx.getText())));
	}

	@Override
	public void exitLeia(LeiaContext ctx) {
		valores.insira(ctx, leia);
	}

	@Override
	public void exitSe(SeContext ctx) {
		final Bool condicao = valores.pegue(ctx.booleano(0));
		final Comando entao = valores.pegue(ctx.comando(0));
		final List<SenaoSe> senaoses = new ArrayList<>();
		for (int i = 1; i < ctx.booleano().size(); i++) {
			Bool b = valores.pegue(ctx.booleano(i));
			Comando c = valores.pegue(ctx.comando(i));
			senaoses.add(new SenaoSe(b, c));
		}
		final Comando senao = valores.pegue(ctx.comando(ctx.comando().size() - 1));
		valores.insira(ctx, new Se(condicao, entao, senaoses, senao));
	}

	@Override
	public void exitInteiro(InteiroContext ctx) {
		valores.insira(ctx, new Inteiro(parseInt(ctx.getText())));
	}

	@Override
	public void exitSkip(SkipContext ctx) {
		valores.insira(ctx, skip);
	}

	@Override
	public void exitEscreva(EscrevaContext ctx) {
		final Expressao exp = valores.pegue(ctx.expressao());
		valores.insira(ctx, new Escreva(exp));
	}

	@Override
	public void exitPrograma(ProgramaContext ctx) {
		final List<Comando> cmds = valores.pegue(ctx.seqComando());
		programa = new Programa(cmds);
		valores.insira(ctx, programa);
	}

	@Override
	public void exitId(IdContext ctx) {
		final String id = ctx.ID().getText();
		valores.insira(ctx, new Id(id));
	}

	@Override
	public void exitSeqComando(SeqComandoContext ctx) {
		final List<Comando> comandos = new ArrayList<>();
		for (ComandoContext c : ctx.comando()) {
			comandos.add(valores.pegue(c));
		}
		valores.insira(ctx, comandos);
	}

	@Override
	public void exitListaId(ListaIdContext ctx) {
		List<String> ids = new ArrayList<>();
		ctx.ID().forEach(t -> ids.add(t.getText()));
		valores.insira(ctx, ids);
	}

	@Override
	public void exitListaExpressao(ListaExpressaoContext ctx) {
		List<Expressao> exps = new ArrayList<>();
		ctx.expressao().forEach(e -> exps.add(valores.pegue(e)));
		valores.insira(ctx, exps);
	}

	@Override
	public void exitAtribuicao(AtribuicaoContext ctx) {
		final List<String> ids = valores.pegue(ctx.listaId());
		final List<Expressao> exps = valores.pegue(ctx.listaExpressao());
		if (ids.size() == 1) {
			valores.insira(ctx, new Atribuicao(ids.get(0), exps.isEmpty() ? new Inteiro(0) : exps.get(0)));
		} else {
			valores.insira(ctx, new AtribuicaoMultipla(ids, exps));
		}
	}

	@Override
	public void exitAtribuicaoCmd(AtribuicaoCmdContext ctx) {
		valores.insira(ctx, valores.pegue(ctx.atribuicao()));
	}

	@Override
	public void exitBloco(BlocoContext ctx) {
		final List<Comando> cmds = valores.pegue(ctx.seqComando());
		valores.insira(ctx, new Bloco(cmds));
	}

	@Override
	public void exitOpAddSub(OpAddSubContext ctx) {
		final Expressao esq = valores.pegue(ctx.expressao(0));
		final Expressao dir = valores.pegue(ctx.expressao(1));
		final String op = ctx.getChild(1).getText();
		final Expressao exp = switch (op) {
			case "+" -> new ExpSoma(esq, dir);
			default -> new ExpSub(esq, dir);
		};
		valores.insira(ctx, exp);
	}

	@Override
	public void exitOpMulDiv(OpMulDivContext ctx) {
		final Expressao esq = valores.pegue(ctx.expressao(0));
		final Expressao dir = valores.pegue(ctx.expressao(1));
		final String op = ctx.getChild(1).getText();
		final Expressao exp = switch (op) {
			case "*" -> new ExpMult(esq, dir);
			default -> new ExpDiv(esq, dir);
		};
		valores.insira(ctx, exp);
	}

	@Override
	public void exitOpPow(OpPowContext ctx) {
		final Expressao esq = valores.pegue(ctx.expressao(0));
		final Expressao dir = valores.pegue(ctx.expressao(1));
		valores.insira(ctx, new ExpPot(esq, dir));
	}

	@Override
	public void exitEnquanto(EnquantoContext ctx) {
		final Bool condicao = valores.pegue(ctx.booleano());
		final Comando comando = valores.pegue(ctx.comando());
		valores.insira(ctx, new Enquanto(condicao, comando));
	}

	@Override
	public void exitPara(ParaContext ctx) {
		final String id = ctx.ID().getText();
		final Expressao inicio = valores.pegue(ctx.expressao(0));
		final Expressao fim = valores.pegue(ctx.expressao(1));
		final Expressao passo = ctx.expressao().size() > 2 ? valores.pegue(ctx.expressao(2)) : null;
		final Comando comando = valores.pegue(ctx.comando());
		valores.insira(ctx, new Para(id, inicio, fim, passo, comando));
	}

	@Override
	public void exitRepita(RepitaContext ctx) {
		Expressao vezes = valores.pegue(ctx.expressao());
		Comando comando = valores.pegue(ctx.comando());
		valores.insira(ctx, new Repita(vezes, comando));
	}

	@Override
	public void exitELogico(ELogicoContext ctx) {
		final Bool esq = valores.pegue(ctx.booleano(0));
		final Bool dir = valores.pegue(ctx.booleano(1));
		valores.insira(ctx, new ELogico(esq, dir));
	}

	@Override
	public void exitOuLogico(OuLogicoContext ctx) {
		final Bool esq = valores.pegue(ctx.booleano(0));
		final Bool dir = valores.pegue(ctx.booleano(1));
		valores.insira(ctx, new OuLogico(esq, dir));
	}

	@Override
	public void exitXorLogico(XorLogicoContext ctx) {
		final Bool esq = valores.pegue(ctx.booleano(0));
		final Bool dir = valores.pegue(ctx.booleano(1));
		valores.insira(ctx, new XorLogico(esq, dir));
	}

	@Override
	public void exitBoolPar(BoolParContext ctx) {
		final Bool booleano = valores.pegue(ctx.booleano());
		valores.insira(ctx, booleano);
	}

	@Override
	public void exitNaoLogico(NaoLogicoContext ctx) {
		final Bool b = valores.pegue(ctx.booleano());
		valores.insira(ctx, new NaoLogico(b));
	}

	@Override
	public void exitExpPar(ExpParContext ctx) {
		final Expressao exp = valores.pegue(ctx.expressao());
		valores.insira(ctx, exp);
	}

	@Override
	public void exitExiba(ExibaContext ctx) {
		if (ctx.TEXTO() != null) {
			final String t = ctx.TEXTO().getText();
			final String texto = t.substring(1, t.length() - 1);
			valores.insira(ctx, new Exiba(texto));
		} else {
			Expressao exp = valores.pegue(ctx.expressao());
			valores.insira(ctx, new Exiba(exp));
		}
	}

	@Override
	public void exitOpRel(OpRelContext ctx) {
		final Expressao esq = valores.pegue(ctx.expressao(0));
		final Expressao dir = valores.pegue(ctx.expressao(1));
		final String op = ctx.relop().getText();
		final Bool exp = switch (op) {
			case "="  -> new ExpIgual(esq, dir);
			case "<=" -> new ExpMenorIgual(esq, dir);
			case "<"  -> new ExpMenor(esq, dir);
			case ">"  -> new ExpMaior(esq, dir);
			case ">=" -> new ExpMaiorIgual(esq, dir);
			case "<>" -> new ExpDiferente(esq, dir);
			default   -> new ExpIgual(esq, esq);
		};
		valores.insira(ctx, exp);
	}

	@Override
	public void exitEscolha(EscolhaContext ctx) {
		Expressao alvo = valores.pegue(ctx.expressao());
		EscolhaCorpoContext corpo = ctx.escolhaCorpo();
		List<Caso> casos = new ArrayList<>();
		for (CasoContext c : corpo.caso()) {
			casos.add(valores.pegue(c));
		}
		Comando outro = corpo.defaultCaso() != null ? valores.pegue(corpo.defaultCaso()) : null;
		valores.insira(ctx, new Escolha(alvo, casos, outro));
	}

	@Override
	public void exitCaso(CasoContext ctx) {
		Expressao valor = valores.pegue(ctx.expressao());
		Comando comando = valores.pegue(ctx.comando());
		valores.insira(ctx, new Caso(valor, comando));
	}

	@Override
	public void exitDefaultCaso(DefaultCasoContext ctx) {
		Comando comando = valores.pegue(ctx.comando());
		valores.insira(ctx, comando);
	}
}
