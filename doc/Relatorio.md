# Relatório de Implementação da Linguagem "Enquanto"

## Escopo
Todas as 10 tarefas descritas no README (divisão e exponenciação, `ou`/`xor`, novas relações, `;` como terminador, `para`, `repita`, `senaose`, `escolha`, atribuição paralela, `exiba` para texto e número). Suporte a funções não foi solicitado nas tarefas e permanece fora do escopo.

## Principais alterações
- **Gramática (`src/plp/enquanto/parser/Enquanto.g4`)**
  - Operadores aritméticos: `/` e `^`.
  - Operadores lógicos: `ou`, `xor`.
  - Relações: `<`, `>`, `>=`, `<>` (além de `=` e `<=`).
  - `;` passa a finalizar comando.
  - Comandos novos: `para ID de exp ate exp (passo exp)? faca comando`, `repita exp vezes comando`, `escolha exp { caso v : comando; outro/_ : comando }`.
  - `se ... entao ... senaose ... senao ...` com suporte a `então` acentuado.
  - Atribuições paralelas: `a, b := 1, 2` (ou `=`).
  - `exiba` aceita texto ou expressão numérica.

- **Semântica (`src/plp/enquanto/Linguagem.java`, `src/plp/enquanto/Regras.java`)**
  - Implementação de `Para`, `Repita`, `Escolha`, `SenaoSe`, `AtribuicaoMultipla`.
  - Novas operações aritméticas (divisão, exponenciação), relações (<, >, >=, <>), e lógicas (`ou`, `xor`).
  - `Exiba` para texto ou número.

- **Testes de exemplo**
  - Ajustes nos arquivos `testes/se2.while`, `testes/se3.while`, `testes/escolha*.while` para refletir a nova sintaxe (`;` onde devido, `entao`).
  - Adicionado `testes/repita1.while` para demonstrar `repita`.

## Como compilar
### PowerShell (Windows)
```powershell
cd "c:\Users\12407421426\Pictures\Lucena\implementa-o-de-linguagens-mateusvitor_riellopes"
java -jar .\lib\antlr-4.13.1-complete.jar -package plp.enquanto.parser .\src\plp\enquanto\parser\Enquanto.g4
javac -cp .\lib\antlr-runtime-4.13.1.jar -d bin .\src\plp\enquanto\parser\*.java .\src\plp\enquanto\*.java
# opcional: gerar jar (requer jar no PATH)
# "C:\caminho\para\jdk\bin\jar.exe" --update --file .\while.jar --main-class plp.enquanto.Principal -C bin plp
```

### Bash (Linux/Mac, equivalente ao compilar.sh)
```bash
cd /caminho/para/implementa-o-de-linguagens-mateusvitor_riellopes
java -jar ./lib/antlr-4.13.1-complete.jar -package plp.enquanto.parser ./src/plp/enquanto/parser/Enquanto.g4
javac -cp ./lib/antlr-runtime-4.13.1.jar -d bin ./src/plp/enquanto/parser/*.java ./src/plp/enquanto/*.java
# opcional: gerar jar
# jar --update --file ./while.jar --main-class plp.enquanto.Principal -C bin plp
```

## Como executar testes/exemplos (PowerShell)
Use `java -cp "bin;lib\antlr-runtime-4.13.1.jar" plp.enquanto.Principal <arquivo.while>`. Exemplos:
```powershell
# exemplos principais
java -cp "bin;lib\antlr-runtime-4.13.1.jar" plp.enquanto.Principal olamundo.while
java -cp "bin;lib\antlr-runtime-4.13.1.jar" plp.enquanto.Principal testes\para1.while
java -cp "bin;lib\antlr-runtime-4.13.1.jar" plp.enquanto.Principal testes\para2.while
java -cp "bin;lib\antlr-runtime-4.13.1.jar" plp.enquanto.Principal testes\para3.while

# condicionais
echo 7 | java -cp "bin;lib\antlr-runtime-4.13.1.jar" plp.enquanto.Principal testes\se1.while
echo 7 | java -cp "bin;lib\antlr-runtime-4.13.1.jar" plp.enquanto.Principal testes\se2.while
java -cp "bin;lib\antlr-runtime-4.13.1.jar" plp.enquanto.Principal testes\se3.while

# escolha
echo 2 | java -cp "bin;lib\antlr-runtime-4.13.1.jar" plp.enquanto.Principal testes\escolha1.while
echo 2 | java -cp "bin;lib\antlr-runtime-4.13.1.jar" plp.enquanto.Principal testes\escolha2.while
echo 0 | java -cp "bin;lib\antlr-runtime-4.13.1.jar" plp.enquanto.Principal testes\escolha3.while

# repita
java -cp "bin;lib\antlr-runtime-4.13.1.jar" plp.enquanto.Principal testes\repita1.while
```

### Observações
- Os arquivos `funcao1.while` a `funcao4.while` continuam sem suporte porque funções não fazem parte das 10 tarefas solicitadas
- O comando `jar` pode não estar no PATH em algumas instalações Windows; use o executável do JDK ou omita o empacotamento e rode direto da pasta `bin` como nos comandos acima.
