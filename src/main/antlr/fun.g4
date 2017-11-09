grammar fun;


// FILE = BLOCK
file
    : block
    ;

// BLOCK = (STATEMENT)*
block
    : (statement)*
    ;

// BLOCK_WITH_BRACES = "{" BLOCK "}"
blockWithBraces
    : '{' block '}'
    ;

// STATEMENT = FUNCTION | VARIABLE | EXPRESSION | WHILE | IF | ASSIGNMENT | RETURN
statement
    : function
    | variable
    | expression
    | while
    | if
    | assignment
    | return
    ;

// FUNCTION = "fun" IDENTIFIER "(" PARAMETER_NAMES ")" BLOCK_WITH_BRACES
function
    : 'fun' IDENTIFIER '(' paramterNames ')' blockWithBraces
    ;

// VARIABLE = "var" IDENTIFIER ("=" EXPRESSION)?
variable
    : 'var' IDENTIFIER ('=' expression)?
    ;

// PARAMETER_NAMES = IDENTIFIER{,}
paramterNames
    : IDENTIFIER {','}
    ;

// WHILE = "while" "(" EXPRESSION ")" BLOCK_WITH_BRACES
while
    : 'while' '(' expression ')' blockWithBraces
    ;

// IF = "if" "(" EXPRESSION ")" BLOCK_WITH_BRACES ("else" BLOCK_WITH_BRACES)?
if
    : 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?
    ;

// ASSIGNMENT = IDENTIFIER "=" EXPRESSION
assignment
    : IDENTIFIER '=' expression
    ;

// RETURN = "return" EXPRESSION
return
    : 'return' expression
    ;

// EXPRESSION = FUNCTION_CALL | BINARY_EXPRESSION | IDENTIFIER | LITERAL | "(" EXPRESSION ")"
expression
    : functionCall
    | binaryExpression
    | IDENTIFIER
    | INT_LITERAL
    | '(' expression ')'
    ;

// FUNCTION_CALL = IDENTIFIER "(" ARGUMENTS ")"
functionCall
    : IDENTIFIER '(' arguments ')'
    ;

// ARGUMENTS = EXPRESSION{","}
arguments
    : expression {','}
    ;

/*
    Арифметическое выражение с операциями: +, -, *, /, %, >, <, >=, <=, ==, !=, ||, &&
    Семантика и приоритеты операций примерно как в Си
*/
binaryExpression
    : expression (op = (MULTIPLY | DIVIDE | MODULUS) expression)*
    | expression op = (PLUS | MINUS) expression
    | expression op = (GT | LT | GTE | LTE) expression
    | expression op = (EQ | NQ) expression
    | expression op = LAND expression
    | expression op = LOR expression
    ;

/*
multiplicativeExpression
    : additiveExpression (op = (MULTIPLY | DIVIDE | MODULUS) additiveExpression)*
    ;

additiveExpression
    : expression (op = (PLUS | MINUS) expression)*
    ;
*/

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
    : ('a'..'z')
    | ('A'..'Z')
    | '_'
    ;

ALPHA_NUM_UNDERSCORE
    : ALPHA_UNDERSCORE
    | ('0'..'9')
    ;

INT_LITERAL
    : ('1'..'9') ('0'..'9')*
    | '0'
    ;
