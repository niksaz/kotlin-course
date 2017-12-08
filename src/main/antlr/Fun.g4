grammar Fun;

file
    : block EOF
    ;

block
    : (statement)*
    ;

blockWithBraces
    : L_CURLY_BRACE block R_CURLY_BRACE
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
    : FUN IDENTIFIER L_BRACE parameterNames R_BRACE blockWithBraces
    ;

variable
    : VAR IDENTIFIER (ASSIGN expression)?
    ;

parameterNames
    : (IDENTIFIER (COMMA IDENTIFIER)*)?
    ;

whileBlock
    : WHILE L_BRACE expression R_BRACE blockWithBraces
    ;

ifStatement
    : IF L_BRACE expression R_BRACE blockWithBraces (ELSE blockWithBraces)?
    ;

assignment
    : IDENTIFIER ASSIGN expression
    ;

returnStatement
    : RETURN expression
    ;

functionCall
    : IDENTIFIER L_BRACE arguments R_BRACE
    ;

arguments
    : (expression (COMMA expression)*)?
    ;

expression
    : lorExpression
    ;

lorExpression
    : landExpression (op = LOR landExpression)*
    ;

landExpression
    : equivalenceExpression (op = LAND equivalenceExpression)*
    ;

equivalenceExpression
    : relationalExpression (op = (EQ | NQ) relationalExpression)*
    ;

relationalExpression
    : additiveExpression (op = (GT | LT | GTE | LTE) additiveExpression)*
    ;

additiveExpression
    : multiplicativeExpression (op = (PLUS | MINUS) multiplicativeExpression)*
    ;

multiplicativeExpression
    : atomicExpression (op = (MULTIPLY | DIVIDE | MODULUS) atomicExpression)*
    ;

atomicExpression
    : functionCall
    | IDENTIFIER
    | NUMBER
    | L_BRACE expression R_BRACE
    ;

L_BRACE : '(';
R_BRACE : ')';
L_CURLY_BRACE : '{';
R_CURLY_BRACE : '}';

COMMA : ',';
ASSIGN : '=';

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

FUN : 'fun';
VAR : 'var';
WHILE : 'while';
IF : 'if';
ELSE : 'else';
RETURN : 'return';

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