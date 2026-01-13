grammar Enquanto;

programa : seqComando EOF?;     // sequência de comandos

seqComando: (comando ';')* comando? ;

comando: atribuicao                                        # atribuicaoCmd
        | 'skip'                                            # skip
                        | 'se' booleano ('entao'|'ent\u00e3o') comando ('senaose' booleano ('entao'|'ent\u00e3o') comando)* 'senao' comando # se
       | 'enquanto' booleano 'faca' comando                # enquanto
       | 'para' ID 'de' expressao 'ate' expressao ('passo' expressao)? 'faca' comando # para
       | 'repita' expressao 'vezes' comando                # repita
       | 'escolha' expressao escolhaCorpo                  # escolha
       | 'exiba' (TEXTO | expressao)                       # exiba
       | 'escreva' expressao                               # escreva
       | '{' seqComando '}'                                # bloco
       ;

escolhaCorpo: (caso)+ defaultCaso? ;
caso: 'caso' expressao ':' comando ;
defaultCaso: ('outro' | '_') ':' comando ;

atribuicao: listaId (':=' | '=') listaExpressao ;
listaId: ID (',' ID)* ;
listaExpressao: expressao (',' expressao)* ;

expressao: expressao '^' expressao                         # opPow
         | expressao ('*' | '/') expressao                 # opMulDiv
         | expressao ('+' | '-') expressao                 # opAddSub
         | INT                                             # inteiro
         | 'leia'                                          # leia
         | ID                                              # id
         | '(' expressao ')'                               # expPar
         ;

booleano: booleano 'e' booleano                            # eLogico
        | booleano 'ou' booleano                           # ouLogico
        | booleano 'xor' booleano                          # xorLogico
        | 'nao' booleano                                   # naoLogico
        | expressao relop expressao                        # opRel
        | '(' booleano ')'                                 # boolPar
        | BOOLEANO                                         # bool
        ;

relop: '=' | '<=' | '<' | '>' | '>=' | '<>' ;

BOOLEANO: 'verdadeiro' | 'falso';
INT: ('0'..'9')+ ;
ID: ('a'..'z')+;
TEXTO: '"' .*? '"';

Comentario: '#' .*? '\n' -> skip;
Espaco: [ \t\n\r] -> skip;