(ns griffin.core-test
  (:require [clojure.test :refer :all]
            [griffin.core :refer :all]))

(deftest test-balanced?
  (are [line-items expected] (= expected (balanced? {:line-items line-items}))
    [{:account-id 1 :amount 10 :type :credit}
     {:account-id 1 :amount 10 :type :debit}
     {:account-id 2 :amount 20 :type :credit}
     {:account-id 2 :amount 20 :type :debit}]
    true

    [{:account-id 1 :amount 10 :type :credit}
     {:account-id 1 :amount 20 :type :debit}
     {:account-id 2 :amount 20 :type :credit}
     {:account-id 2 :amount 20 :type :debit}]
    false

    [{:account-id 1 :amount 10 :type :credit}
     {:account-id 1 :amount 10 :type :debit}
     {:account-id 2 :amount 20 :type :credit}
     {:account-id 1 :amount 20 :type :debit}]
    false

    [{:account-id 1 :amount 10 :type :debit}
     {:account-id 1 :amount 10 :type :debit}
     {:account-id 2 :amount 20 :type :debit}
     {:account-id 1 :amount 20 :type :debit}]
    false
    ))
