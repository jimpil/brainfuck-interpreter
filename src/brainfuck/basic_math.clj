(ns brainfuck.basic-math
  (:require [brainfuck.core :refer [compile-bf]]))

;; ADDITION (+)
(let [bf-add (compile-bf ",>,[<+>-]<")]
  (defn plus
    "Addition wrapper which delegates to the brainfuck compiled fn."
    ([x y]
     (cond
       (and (neg? x)
            (neg? y)) (- (bf-add (- x) (- y)))
       (neg? y) (bf-add y x)
       :else
       (bf-add x y)))
    ([x y & more]
     (reduce plus (plus x y) more))))

;; SUBTRACTION (-)
(defn minus
  "Subtraction implemented on top of addition."
  ([x y]
   (plus (- y) x))
  ([x y & more]
   (reduce minus (minus x y) more)))


;; MULTIPLICATION (*)
(let [bf-mult (compile-bf ",>,<[>[>+>+<<-]>>[-<<+>>]<<<-]>>")]
  (defn times
    "Multiplication wrapper which delegates to the brainfuck compiled fn.
    (https://learnxinyminutes.com/docs/brainfuck/)"
    ([x y]
     (cond
       (and (neg? x)
            (neg? y)) (bf-mult (- x) (- y))

       (and (<= 0 x)
            (<= 0 y))  (bf-mult x y)
       :else
       (if (neg? x)
         (- (bf-mult (- x) y))
         (- (bf-mult x (- y))))))
    ([x y & more]
     (reduce times (times x y) more))))

;; EXPONENTIATION (^)
(defn mult-pow [x y]
  (let [pos-exponent? (pos? y)
        x (if pos-exponent? x (- x))
        xs (repeat (Math/abs (long y)) x)]
    (apply times xs)))

;; the above works better
(let [bf-pow (compile-bf ",>,>+<[->[-<<[->>>+>+<<<<]>>>>[-<<<<+>>>>]<<]>[-<+>]<<]>")]
  (defn pow
    ([x y]
     (bf-pow x y))))


;;DIVISION (/)
(let [bf-divide (compile-bf ",>,<[>[->+>+<<]>[-<<-[>]>>>[<[-<->]<[>]>>[[-]>>+<]>-<]<<]>>>+<<[-<<+>>]<<<]>>>>>[-<<<<<+>>>>>]<<<<<")]
  (defn div [x y]
    (cond
      (and (neg? x)
           (neg? y)) (bf-divide (- x) (- y))
      (and (<= 0 x)
           (<= 0 y)) (bf-divide x y)
      :else
      (if (neg? x)
        (- (bf-divide (- x) y))
        (- (bf-divide x (- y)))))))


(let [bf-modulo (compile-bf ",>,<[>[->+>+<<]>[-<<-[>]>>>[<[-<->]<[>]>>[[-]>>+<]>-<]<<]>>>+<<[-<<+>>]<<<]>>>>>[-<<<<<+>>>>>]<<<<")]
  (defn modulo [x y]
    (cond
      (and (neg? x)
           (neg? y)) (bf-modulo (- x) (- y))
      (and (<= 0 x)
           (<= 0 y)) (bf-modulo x y)
      :else
      (if (neg? x)
        (- (bf-modulo (- x) y))
        (- (bf-modulo x (- y)))))))
