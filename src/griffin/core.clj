(ns griffin.core
  (:require [clojure.spec.alpha :as s])
  (:gen-class))

(s/def ::id uuid?)
(s/def ::account-id uuid?)
(s/def ::type #{:credit :debit})
(s/def ::amount pos-int?)
(s/def ::line-item (s/keys :req-un [::account-id ::amount ::type]))
(s/def ::line-items (s/coll-of ::line-item :min-count 2))
(s/def ::journal-entry (s/keys :req-un [::id ::line-items]))


(s/fdef calculate-balance
  :ret int?)
(defn- calculate-balance
  [[_ line-items]]
  (let [accumulate (fn [acc {:keys [amount type]}]
                     (+ acc (case type
                              :debit amount
                              :credit (* -1 amount))))]
    (reduce accumulate 0 line-items)))

(s/fdef balanced?
  :args ::journal-entry
  :ret boolean?)
(defn balanced?
  [{:keys [line-items]}]
  (->> line-items
       (group-by :account-id)
       (map calculate-balance)
       (every? zero?)))
