(ns griffin.core
  (:gen-class))

(defn- calculate-balance
  [[_ line-items]]
  (let [accumulate (fn [acc {:keys [amount type]}]
                     (+ acc (case type
                              :debit amount
                              :credit (* -1 amount))))]
    (reduce accumulate 0 line-items)))

(defn balanced?
  [{:keys [line-items]}]
  (->> line-items
       (group-by :account-id)
       (map calculate-balance)
       (every? zero?)))
