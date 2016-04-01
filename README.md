# brainfuck-interpreter

## Usage

```clj
;; Given some brainfuck source code, we can compile its instructions to a Clojure function. 
;; If the program takes input (via `,`), we have the option of choosing whether the input values should arrive as parameters, or via stdin:
;; For example the following 2 functions do the same thing, but the former expects to be called with 2 arguments, as opposed to the latter which takes no arguments 
;; and rather waits for values to come from stdin: 

(def bf-plus 
  (compile-bf :parameterized ",>,[<+>-]<"))
  
$ (bf-plus 3 5) => 8  
  
(def bf-plus 
  (compile-bf :interactive ",>,[<+>-]<"))

```

## Basic math

Namespace brainfuck.basic-math contains implementations of +, -, * /, ^, % compiled straight from brainfuck source code. 

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
