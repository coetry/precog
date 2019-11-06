(ns precog.parse
  #?(:cljs (:require 
            [cljs-bean.core]
            ["preact" :as preact])))

(declare fragment)

#?(:cljs
   (defn fragment [children]
     (apply preact/createElement preact/Fragment #js {} children)))

(defn ele [el ref key props]
  `(cljs.core/js-obj "constructor" js/undefined
                     "type" ~el
                     "ref" ~ref
                     "key" ~key
                     "props" (if (or (string? ~el) (.-__precog--use-bean ~el))
                               (cljs-bean.core/->js (merge (cljs-bean.core/bean) ~props))
                               ~props)))

(defn parse [form]
  (cond
    (vector? form)
    (let [[cmp & prpchl] form
          el             (cond (keyword? cmp) (name cmp)
                               :else cmp)
          props?         (map? (first prpchl))
          props          (if props? (first prpchl) {})
          children       (mapv parse
                               (if props? (rest prpchl) prpchl))]
      (if (contains? #{"<" "<>"} el)
        `(fragment ~children)
        (ele el
             (:ref props)
             (:key props)
             (-> props
                 (dissoc :ref :key)
                 (assoc :children children)))))

    (list? form)
    (case (first form)
      list
      (parse (into ["<>"] (map parse (rest form))))

      (do let for when when-not when-let when-first when-some)
      (concat (butlast form)
              (list (parse (last form))))

      (if if-not if-let if-some)
      (concat (take 2 form)
              (map parse (take-last 2 form)))

      case
      (concat (take 2 form)
              (mapcat (fn [[clause expr]] [clause (parse expr)])
                      (->> form (drop 2) (butlast) (partition 2)))
              (list (parse (last form))))

      cond
      (conj (mapcat (fn [[clause expr]] [clause (parse expr)])
                    (->> form rest (partition 2)))
            (first form))

      form)

    :else
    form))