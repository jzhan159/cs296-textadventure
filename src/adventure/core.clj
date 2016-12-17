(ns adventure.core
  (:require [clojure.core.match :refer [match]]
            [clojure.string :as str])
  (:gen-class))

(def the-map
  { :ship-cockpit {:desc "You wake up in the cockpit of an abandoned space ship. There are smells of burned stuff all over the space, and the lights are off. The control panel in front of you have a red light blinking. You have box in your hand with a puzzle on it. Maybe there is a critical item in there?"
           :title "in the cockpit"
           :requirements ""
           :dir {:forward :cockpit-door-locked, :turn-on :ship-cockpit-on}
           :contents {:g (atom 0), :w (atom nil), :a "Gloves"}
           :flag 1
    }

    :ship-cockpit-on {:desc "You turned on the control panel, the cockpit suddenly lights up. On the wall are some holes created by flying bullets. A huge screen behind you is slightly on fire. There is a monitor in front of you saying:\nService ID: mattox\nPasscode  : CS296H"
               :title "in the cockpit"
               :requirements ""
               :dir {:forward :cockpit-door-locked}
               :contents {:a "Smart Card", :g (atom 5), :w (atom nil)}
               :flag 1
    }

   :cockpit-door-locked {:desc "You encountered a locked door. Maybe you can find something to UNLOCK the door."
              :title "at the cockpit door"
              :requirements ""
              :dir {:back :ship-cockpit}
              :contents {:g (atom 0), :w (atom nil), :a "Dust"}
              :flag 9
   }
   :cockpit-door {:desc "The door is open, the sign on the end of way shows EMERGENCY EXIT in red."
              :title "at the cockpit door"
              :requirements "Smart Card"
              :dir {:forward :corridor}
              :contents {:g (atom 0), :w (atom nil)}
              :flag 1
   }
   :corridor {:desc "The door behind you is locked up by some kind of remote control. You cannot go back to the cockpit again. The corridor shows some sign of explotion. You realized that your spaceship has been taken by the aliens. In the end are two ways, LEFT leads to the Armory, RIGHT leads to the Engine Maintainance Room."
              :title "in the corridor"
              :requirements ""
              :dir {:left :armory, :right :engine, :forward :corridor2}
              :contents {:g (atom 0), :w (atom nil), :a "a book"}
              :flag 1
   }
   :armory {:desc "There are several weapons on the shelf, but you need money to unlock them."
              :title "in the armory"
              :requirements ""
              :dir {:buy :armory, :back :corridor}
              :contents {:g (atom 0), :w (atom ""), :a "Bullets"}
              :flag 8
   }
   :engine {:desc "The room is filled with smoke. You must get some first aid kit."
              :title "in the engine room"
              :requirements ""
              :dir {:back :corridor}
              :contents {:g (atom 0), :a "First Aid", :w (atom nil)}
              :flag 2
   }
   :corridor2 {:desc "You are still in the corridor and just see something passing by. What is it? Hope that's just my illusion......"
              :title "in the corridor"
              :requirements ""
              :dir {:forward :stairs, :back :corridor}
              :contents {:g (atom 0), :w (atom nil)}
              :flag 1
   }
   :stairs {:desc "You are at the staircase. The meeting room is upstairs."
              :title "at the staircase"
              :requirements ""
              :dir {:up :meeting-room, :back :corridor2, :forward :dining-hall}
              :contents {:g (atom 0), :w (atom nil)}
              :flag 1
   }
   :meeting-room {:desc "You see the captain is lying on the bloody groud, dead. You then hear some gunshots. There are lights coming through above your head. Maybe upstairs is a way out."
              :title "in the meeting room"
              :requirements ""
              :dir {:down :stairs, :up :outside}
              :contents {:g (atom 0), :w (atom nil)}
              :flag 1
   }
   :outside {:desc "You see some huge alien space flg ship outside, they are attacking earth! There is a little life ship several steps away attached to the spaceship. You remember that you have to destroy the ship before you can abandon it. Maybe the engine can help."
              :title "outside in the space"
              :requirements ""
              :dir {:down :meeting-room, :forward :life-ship}
              :contents {:g (atom 0), :w (atom nil)}
              :flag 1
   }
   :life-ship {:desc "You are now in the life ship. "
              :title "in the life ship"
              :requirements ""
              :dir {:back :outside}
              :contents {:g (atom 0), :w (atom nil)}
              :flag 0
   }
   :dining-hall {:desc "This is the dining hall. You heard noise of breathing. Who is there? Somebody suddenly start to shoot at you. Looks like some kind of alien weapon."
                :title "in the dining hall"
                :requirements ""
                :dir {:back :stairs, :forward :kitchen}
                :contents {:g (atom 0), :w (atom nil) :a "Food"}
                :flag 3
                :alive (atom 1)
   }
   :kitchen {:desc "This is the kitchen, it looks like there was an intense fight."
                :title "in the kitchen"
                :requirements ""
                :dir {:back :dining-hall, :forward :maintainance-tunnel}
                :contents {:g (atom 0), :w (atom nil)}
                :flag 1
   }
   :maintainance-tunnel {:desc "This is the maintainance tunnel, a transport system designed for space ship maintainance, but you can easily get through."
                :title "in the tunnel"
                :requirements ""
                :dir {:back :kichen, :forward :maintainance-tunnel2}
                :contents {:g (atom 10), :w (atom nil), :a "Screw Driver"}
                :flag 1
   }
   :maintainance-tunnel2 {:desc "You are still in the maintainance tunnel. It looks like somebody has been here a moment ago. Something is on the ground."
                :title "in the tunnel"
                :requirements ""
                :dir {:back :maintainance-tunnel, :forward :stairs}
                :contents {:g (atom 0), :a "AK-47"}
                :flag 1
   }

   })

(def zero 0)


(def adventurer
  {:location :ship-cockpit
   :inventory #{""}
   :tick (atom 10)
   :wallet 0
   :seen #{}
   :HP 100
   :count-down 0})

(defn status [player]
  (let [location (player :location)]
    (print (str "You are " (-> the-map location :title) ". ")
    )
    (when-not ((player :seen) location)
      (print (-> the-map location :desc)
      )
    )
    (update-in player [:seen] #(conj % location)
    )
  )
)

(defn check-bag [player]
  (let [num (count (player :inventory))]
    (do (println "You now have " (- num 1) " item(s).")
        player
    )
  )
)

(defn check-money [player]
  (let [num (player :wallet)]
    (do (println "You now have " num " gold")
        player
    )
  )
)

(defn check-HP [player]
  (let [curr (player :HP)]
    (do (println "You now have " curr " HP.")
        player
    )
  )
)

(defn check-time [player]
  (if ( = (player :count-down) 0)
    (do (println "Timer not set.") player)
    (let [num (deref (player :tick))]
      do (println "The ship will explode in " num "s. Hurry up!")
      player
    )
  )
)

(defn get-object [player]
  (let [location (player :location) object (-> the-map location :contents)]
    (if (nil? (object :a))
      (do (println "Nothing to get. ")
          player)
      (if (contains? (player :inventory) (object :a))
        (do (println "You've already get the item here. ")
            player)
        (do (println (str "You get the " (object :a) "."))
          (update-in player [:inventory] #(conj % (object :a)))
        )
      )
    )
  )
)

(defn get-money [player]
  (let [location (player :location), money (-> the-map location :contents :g), update (deref money)]
    (if (zero? (deref money))
      (do (println "No money here. ")
          player)
      (do (println (str "You get " update " gold."))
          (reset! money 0)
          (update-in player [:wallet] + update)
      )
    )
  )
)

(defn buy-weapon [player]
  (let [location (player :location)]
  (if ( = (-> the-map location :flag) 8)
      (if (zero? (player :wallet))
        (do (println "You are so poor. You do not have enough gold. ")
            player)
        (do (println (str "Thank you for your order. Your Assault Rifle is ready for pick up."))
            (reset! (-> the-map :armory :contents :w) "AK-47")
            (update-in player [:wallet] - 5)
        )
      )
      (do (println "You cannot buy or pick up anything here.")
        player
      )
  )
  )
)

(defn pick-up [player]
  (let [location (player :location)]
  (if ( = (-> the-map location :flag) 8)
  (if (= (deref (-> the-map location :contents :w)) "")
      (do (println "You need to buy the weapon first.") player)
      (do (println "You get the " (deref (-> the-map location :contents :w)) ) (update-in player [:inventory] #(conj % (deref (-> the-map location :contents :w))))
      )
  )
  (do (println "You cannot buy or pick up anything here.") player))
  )
)


(defn to-keywords [commands]
  (mapv keyword (str/split commands #"[.,?! ]+")))

  (defn tock [player]
    (reset! (player :tick) (- (deref (player :tick)) 1)))

  (defn go [dir player]
    (let [location (player :location)
          dest (->> the-map location :dir dir)]
      (if (= (player :count-down) 1) (tock player) (do (print "")))
      (if (= (deref (player :tick)) 0) (println "Boooooom=3 The ship exploded. You are dead now :p\nBad ending.(Press ctrl+c now or your computer will exploooooode)") (do (print "")))
      (if (nil? dest)
        (do (println "You can't do that. ")
            player)
        (if (contains? (player :inventory) (-> the-map location :requirements))
              (assoc-in player [:location] dest)
                (do (println "You don't have enough required items do that. ")
                  player))
      )
    )
  )

(defn shoot [player]
  (let [location (player :location)] (if (contains? (player :inventory) "AK-47") (
    if (= (-> the-map location :flag) 3)
    (if (= (-> the-map location :alive deref) 0) (println "The enemy is dead, do not waste time.") (do (reset! (-> the-map location :alive) 0) (println "You killed the enemy.")))
    (println "You are wasting bullets.")
    ) (println "You need to have a weapon to do that.")))player)

(defn start-engine [player]
  (let [location (player :location)] (if ( = (-> the-map location :flag) 0)
  (if (zero? (player :count-down))
  (do (println "You abandoned the space ship, aliens have captured intels about earth, your home land is going to be destroyed. Bad ending :-") player)
  (do (println "You escaped and saved the earth for now. The name Mattox will be remembered by the whole human race!\nGo back to your army and fight for your homeland!\nCongratulation! You win the game.\n")
  player))
  (do (println "I don't understand you.") player)
  )
))

(defn override-engine [player]
  (let [location (player :location)] (if ( = (-> the-map location :flag) 2)
  (if (> (player :count-down) 0)
  (do (println "Engine overided, count down in progress, please proceed to evac ship as soon as possible!.\nRemaining time: " (deref (player :tick))) player)
  (do (println "self-destroy program initiated. Count down 10s, starting now. Please proceed to evac point now.") (update-in player [:count-down] inc)))
  (do (println "I don't understand you.") player)
    )
  )
)

(defn solve [player]
  (if (contains? (player :inventory) "a beautiful key")
  (do (println "You have already solved the puzzle. sjb") player)
  (let [answer (read-line)]
    (if (= answer "oneday")
        (do (println "You got the right answer and with a flash of light the box is opened. (Although the answer is just a joke.)")
            (update-in player [:inventory] #(conj % "a beautiful key"))
        )
        (do (println "You got the wrong answer. Reconsider.")
            (solve player)
        )
      )
    )
  )
)

(defn help [player]

  (println "<supported commands>\n

move instructions:  <forward>/<f> —— go forward\n
                    <back>/<b> —— go backward\n
                    <left> —— go left\n
                    <right> —— go right\n
                    <up> —— go upstairs\n
                    <down> —— go downstairs\n
                    <get> —— get the object in the room if there is any\n
                    <rob> —— get the money in the room\n
                    <buy> —— use your money to buy important inventories\n
                    <pickup> —— pickup the item you just buy\n
                    <turn-on>/<on> —— turn on the light to see the surroundings\n
                    <unlock> —— unlock the doors with specific keys\n
                    <look> —— check your current situation and location\n
                    <start-enging> —— start the engine\n
                    <override-engine> —— overrides the main engine, self-destroy\n
                    <shoot> —— use weapon to kill enemy\n
                    <scrutinise> —— take a look at the box\n
                    <solve> —— try to solve the puzzle on the box\n
                    <bag> —— see how many inventories you have\n
                    <checkHP> —— check your HP\n
                    <wallet> —— check how many money you have\n
                    <time> —— tick tock tick tock what is the count down for?\n
      P.S. There is a hidden feature. Figure it out! Good luck!\n"
      )player)

(defn unlock [player]
  (let [location (player :location)]
  (if (= (-> the-map location :flag) 9)
      (if (contains? (player :inventory) "a beautiful key")
           (assoc-in player [:location] :cockpit-door)
           (do (println "You need a key to unlock the door") player))
       (do (println "There's nothing to unlock.") player))))

(defn respond [player command]
  (match command
         [:bag] (check-bag player)
         [:look] (update-in player [:seen] #(disj % (-> player :location)))
         [(:or :f :forward)] (go :forward player)
         [(:or :b :back)] (go :back player)
         [:left] (go :left player)
         [:right] (go :right player)
         [:up] (go :up player)
         [:down] (go :down player)
         [(:or :on :turn-on)] (go :turn-on player)
         [:get] (get-object player)
         [:loveClojure] (do (println "Cheat activated!") (update-in player [:HP] + 10))
         [:checkHP] (check-HP player)
         [:unlock] (unlock player)
         [:rob] (get-money player)
         [:buy] (buy-weapon player)
         [:start-engine] (start-engine player)
         [:wallet] (check-money player)
         [:override-engine] (override-engine player)
         [:time] (check-time player)
         [:shoot] (shoot player)
         [:pickup] (pick-up player)
         [:scrutinise] (do (println "It is a elaborate box with a puzzle on it -- What is the shortest time to learn a new programming language and write a game with it? There is a keypad on it maybe you can type in the answer and get something from the box.") player)
         [:solve] (do (println "If you have the answer for the puzzle, then type in the answer. (hint: only six characters)") (solve
                      player))
         [:help] (help player)
         _ (do (println "I don't understand you.")
               player)
  )
)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (loop [local-map the-map
         local-player adventurer]
    (let [pl (status local-player)
          _  (println "\n  What do you want to do?")
          command (read-line)]
      (recur local-map (respond pl (to-keywords command))))))
