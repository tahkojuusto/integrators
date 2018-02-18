(ns integrator.parser.lex
    (:require [clojure.tools.logging :as log]
              [clojure.string :as str]))

(def add-operators      #{\+ \-})
(def mult-operators     #{\* \/})
(def math-functions     #{"exp" "ln" "sqrt"})
(def left-bracket       \()
(def right-bracket      \))
(def variables          #{\x \y})

(defn token [type value] {:type type :value value})

(defn left-bracket-token []         (token "l-par" "("))
(defn right-bracket-token []        (token "r-par" ")"))
(defn add-operator-token [val]      (token "op-add" val))
(defn mult-operator-token [val]     (token "op-mult" val))
(defn math-function-token [val]     (token "math-fn" val))
(defn variable-token [val]          (token "var" val))
(defn number-token [val]            (token "val" val))

(defn is-whitespace? [c]        (clojure.string/blank? (str c)))
(defn is-left-bracket? [c]      (= left-bracket c))
(defn is-right-bracket? [c]     (= right-bracket c))
(defn is-add-operator? [c]      (contains? add-operators c))
(defn is-mult-operator? [c]     (contains? mult-operators c))
(defn is-math-function? [func]  (contains? math-functions func))
(defn is-variable? [c]          (contains? variables c))
(defn is-number? [c]            (Character/isDigit c))

(declare -scan)

(defn -lex-math-function [input-str tokens]
    (let [math-fn-name (some #(and (str/starts-with? input-str %) %) math-functions)]
        (-scan (str/replace-first input-str math-fn-name "") "" (conj tokens (math-function-token math-fn-name)))))

(defn -scan
    "Given function as string and in infix expression, create tokens from it."
    [input-str peek tokens]
    (log/trace "ENTERING lex/-scan.")
    (let [c    (first input-str) ; first char in the input string.
          peek (str peek c)
          input-str (str/join input-str)] ; buffered first chars comprising a part of token value.
        (cond
              ; Input string is empty, the whole expression is read.
              ; Return all found tokens.
              (not c)               tokens

              ; Continue with the input string if there are whitespaces.
              (is-whitespace? c)    (-scan (rest input-str) "" tokens)

              ; Detect brackets.
              (is-left-bracket? c)  (-scan (rest input-str) "" (conj tokens (left-bracket-token)))
              (is-right-bracket? c) (-scan (rest input-str) "" (conj tokens (right-bracket-token)))

              ; Detect operators.
              (is-add-operator? c)      (-scan (rest input-str) "" (conj tokens (add-operator-token peek)))
              (is-mult-operator? c)     (-scan (rest input-str) "" (conj tokens (mult-operator-token peek)))

              ; Detect math functions.
              (some #(str/starts-with? input-str %) math-functions)     (-lex-math-function input-str tokens)

              ; Detect variable 'x'.
              (is-variable? c)      (-scan (rest input-str) "" (conj tokens (variable-token peek)))

              ; Detect integers.
              (is-number? c)        (let [second-c (second input-str)]
                                        ; Push token if the second next char is not a digit.
                                        ; Otherwise, continue buffering first chars.
                                        (if (not (and second-c (is-number? second-c)))
                                            (-scan (rest input-str) "" (conj tokens (number-token (Integer/parseInt peek))))
                                            (-scan (rest input-str) peek tokens)))

              ; No valid symbol, stop the lexer.
              :else (throw (Exception. (str "Unknown token: " peek))))))

(defn scan
    "Given function as string and in infix expression, create tokens from it."
    [input-str]
    (log/trace "ENTERING lex/scan.")
    (-scan input-str "" []))