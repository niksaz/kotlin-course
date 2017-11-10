grammar Fun;


file
    : block
    ;

block
    : (statement)*
    ;

blockWithBraces
    : '{' block '}'
    ;

statement
    : function
    | variable
    | expression
    | whileBlock
    | ifStatement
    | assignment
    | returnStatement
    ;

function
    : 'fun' IDENTIFIER '(' (parameterNames)? ')' blockWithBraces
    ;

variable
    : 'var' IDENTIFIER ('=' expression)?
    ;

parameterNames
    : IDENTIFIER (',' IDENTIFIER)*
    ;

whileBlock
    : 'while' '(' expression ')' blockWithBraces
    ;

ifStatement
    : 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?
    ;

assignment
    : IDENTIFIER '=' expression
    ;

returnStatement
    : 'return' expression
    ;

functionCall
    : IDENTIFIER '(' (arguments)? ')'
    ;

arguments
    : expression (',' expression)*
    ;

expression
    : atomicExpression
    | binaryExpression
    ;

binaryExpression
    : atomicExpression op = (MULTIPLY | DIVIDE | MODULUS) expression
    | atomicExpression op = (PLUS | MINUS) expression
    | atomicExpression op = (GT | LT | GTE | LTE) expression
    | atomicExpression op = (EQ | NQ) expression
    | atomicExpression op = LAND expression
    | atomicExpression op = LOR expression
    ;

atomicExpression
    : functionCall
    | IDENTIFIER
    | NUMBER
    | '(' expression ')'
    ;

MULTIPLY : '*';
DIVIDE : '/';
MODULUS : '%';

PLUS : '+';
MINUS : '-';

GT : '>';
LT : '<';
GTE : '>=';
LTE : '<=';

EQ : '==';
NQ : '!=';

LOR : '||';
LAND : '&&';

NUMBER
    : ([1-9] [0-9]*)
    | '0'
    ;

KEYWORDS
    : 'fun'
    | 'var'
    | 'while'
    | 'if'
    | 'else'
    | 'return'
    ;

IDENTIFIER
    : ALPHA_UNDERSCORE (ALPHA_NUM_UNDERSCORE)*
    ;

ALPHA_UNDERSCORE
    : [a-z]
    | [A-Z]
    | '_'
    ;

ALPHA_NUM_UNDERSCORE
    : ALPHA_UNDERSCORE
    | [0-9]
    ;

COMMENT
    : '//' ~[\r\n]* -> skip
    ;

WS
    : (' ' | '\t' | '\r'| '\n') -> skip
    ;
