(ns precog.state
  (:require
   [cljs-bean.core :as bean :refer [->clj bean]]
   ["preact/hooks" :as hooks]))

(defn use-lens [*store f]
  (let [[value update-value] (hooks/useState (f @*store))]
    (hooks/useEffect
     (fn [_]
       (let [k (gensym "useLens")]
         (add-watch *store k
                    (fn update-lens-hook [_ _ _ new-state]
                      (update-value (f new-state))))
         (fn [] (remove-watch *store k)))))
    value))

(defn use-atom [default-val]
  (let [[*store update-store] (hooks/useState (fn [] (atom default-val)))
        [value update-value]  (hooks/useState @*store)]
    (hooks/useEffect
     (fn [_]
       (let [k (gensym "useAtom")]
         (add-watch *store k
                    (fn update-atom-hook [_ _ _ new-state]
                      (update-value new-state)))
         (fn [] (remove-watch *store k)))))
    *store))

(defn- xevent [flags]
  (fn [event]
    (when (contains? flags :stop)
      (.stopPropagation event)
      (.preventDefault event))
    (if (contains? flags :target)
      (->clj (bean (.-target event)))
      event)))

(defn bind-handler 
  ([*store event-flags f & args]
   (let [xe (if (empty? event-flags)
              identity
              (xevent event-flags))]
     (hooks/useMemo
      (fn [] (fn [e] (apply swap! *store f (xe e) args)))
      #js [*store]))))
