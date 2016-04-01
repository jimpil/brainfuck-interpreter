(ns brainfuck.core
  (:gen-class)
  (:import (java.util.concurrent.atomic AtomicLong)))

(defn- find-bracket
  [^String code opening-bracket closing-bracket instruction-pointer direction]
    (loop [i (direction instruction-pointer)
           opened 0]
      (condp = (.charAt code i)
        opening-bracket (recur (direction i) (inc opened))
        closing-bracket (if (zero? opened)
                          i
                          (recur (direction i) (dec opened)))
        (recur (direction i) opened))))

(defn- bf-interpreter
  "A brainfuck interpreter which reads from args rather than `System.in();`"
  [mode ^String code args]
  (let [arg-index (AtomicLong. 0)
        mode (or mode :parameterized)
        read-input (case mode
                     :parameterized #(nth args (.getAndIncrement arg-index))
                     :interactive #(Long/parseLong (read-line)))]
    (loop [cells [0]
           pos 0
           instruction-pointer 0]
      (case (try (.charAt code instruction-pointer)
                 (catch StringIndexOutOfBoundsException _ ::end))
        \<  (recur cells (dec pos) (inc instruction-pointer))
        \+  (recur (update cells pos inc) pos (inc instruction-pointer))
        \-  (recur (update cells pos dec) pos (inc instruction-pointer))

        \>  (let [next-ptr (inc pos)]
              (recur (if (= next-ptr (count cells))
                       (conj cells 0)
                       cells)
                     next-ptr
                     (inc instruction-pointer)))

        \.  (do
              (print (char (get cells pos)))
              (recur cells pos (inc instruction-pointer)))
        \,  (recur (assoc cells pos (read-input)) pos (inc instruction-pointer))
        \[  (recur cells pos (inc (if (zero? (get cells pos))
                                    (find-bracket code \[ \] instruction-pointer inc)
                                    instruction-pointer)))
        \]  (recur cells pos (find-bracket code \] \[ instruction-pointer dec))
        ;; end of program  - return the value in <cells> at index <pos>
        ::end (get cells pos)
        ;; could return the full map for debugging
          #_{:pos pos
             :cells cells
             :return (get cells pos)}
        ;;default pass-through case
        (recur cells pos (inc instruction-pointer))))))

(defn compile-bf
  "The top-level 'compiler'. It uses the bf code to build a clojure function called `run-bf` accepting varargs."
  ([code]
   (compile-bf :parameterized code))
  ([mode code]
   (fn run-bf [& args]
     (bf-interpreter mode code args))))



;======================<MAIN>====================================
(defn -main [& args]
  (let [source-code-mode (first args)
        program-file (second args)]
    (assert (and program-file source-code-mode)
            "Please specify a brainfuck source mode [file VS source] as the first argument and the actual source  as the second...")
    (println ((compile-bf :interactive (cond-> program-file
                                               (= source-code-mode "file") slurp))))))

(comment
  ;; https://nvbn.github.io/2015/04/30/brainfuck-clojure/
  (def hello-world-no-loops
    "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.+++++++++++++++++++++++++++++.+++++++..+++.-------------------  ------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++++++++++++++++.+++.------.--------.-------------------------------------------------------------------.-----------------------.")
  (def hello-world-with-loops
    "++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.")
  )
