(ns tamagotchi-kata-clojure.command-line
  (:gen-class)
  (:require [clojure.string :as str]
            [clansi])
  (:use [tamagotchi-kata-clojure.core :as tamagotchi]))

(def commands
  [{:name "show" :desc "shows your tamagotchi status"}
   {:name "feed" :desc "feeds your tamagotchi"}
   {:name "play" :desc "play with your tamagotchi"}
   {:name "bed"  :desc "puts your tamagotchi to bed"}
   {:name "poo"  :desc "makes your tamagotchi poo"}
   {:name "quit" :desc "quits - and your tamagotchi dies"}])

(defn format-command-name [name]
  (clansi/style name :green))

(defn format-attribute-value [value]
  (clansi/style value :green))

(defn describe-command [{:keys [name desc]}]
  (println (format-command-name (format "%-5s" name)) desc))

(defn show-valid-commands []
  (println "Valid commands are: " (str/join " | " (map #(format-command-name(:name %)) commands))))

(defn prompt-menu []
  (doall (map describe-command commands)))

(defn- show-status [tamagotchi]
  (println "name:" (:name @tamagotchi)
           " | hungriness:" (format-attribute-value (:hungriness @tamagotchi))
           " | fullness:" (format-attribute-value (:fullness @tamagotchi))
           " | happiness:" (format-attribute-value (:happiness @tamagotchi))
           " | tiredness:" (format-attribute-value (:tiredness @tamagotchi))
           ))

(declare ui-loop)

(defn- dispatch [command]
  (case command

    :show
    (show-status tamagotchi)

    :feed
    (do (tamagotchi/feed)
        (dispatch :show))

    :play
    (do (tamagotchi/play)
        (dispatch :show))

    :bed
    (do (tamagotchi/put-to-bed)
        (dispatch :show))

    :poo
    (do (tamagotchi/make-poop)
        (dispatch :show))

    :quit
    (System/exit 0)

    ;; otherwise
    (show-valid-commands))

  (ui-loop))


(defn- ui-loop []
  (let [[command-str & _] (str/split (read-line) #" ")
        command (keyword command-str)]
    (dispatch command)))

(defn- init-tamagotchi []
  (do
    (println "Name your tamagotchi [Miyagi]")
    (let [[name & _] (str/split (read-line) #" ")]
      (if (str/blank? name)
        (tamagotchi/create)
        (tamagotchi/create :name name))
      (dispatch :show))))

(defn -main [& args]
  (prompt-menu)
  (init-tamagotchi)
  (ui-loop))