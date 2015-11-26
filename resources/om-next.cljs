(ns $PROJECT_NAME_HYPHENATED$.core
  (:require [om.next :as om :refer-macros [defui]])
  (:require-macros [natal-shell.components :refer [view text image touchable-highlight]]
                   [natal-shell.alert-ios :refer [alert]]))

(set! js/React (js/require "react-native/Libraries/react-native/react-native.js"))

(defonce app-state (atom {:app/msg "Welcome to $PROJECT_NAME$"}))

(defui WidgetComponent
  static om/IQuery
  (query [this]
         '[:app/msg])
  Object
  (render [this]
    (try
      (let [{:keys [app/msg]} (om/props this)]
        (view {:style {:flexDirection "column" :margin 40 :alignItems "center"}}
          (text
            {:style {:fontSize 50 :fontWeight "100" :marginBottom 20 :textAlign "center"}}
            msg)

          (image {:source {:uri "https://raw.githubusercontent.com/cljsinfo/logo.cljs/master/cljs.png"}
                  :style {:width 80 :height 80 :marginBottom 30}})

          (touchable-highlight
            {:style {:backgroundColor "#999" :padding 10 :borderRadius 5}
             :onPress #(alert "HELLO!")}
            (text {:style {:color "white" :textAlign "center" :fontWeight "bold"}} "press me"))))

      (catch :default e
        (view {:style {:backgroundColor "#cc0814" :flex 1 :padding 20 :paddingTop 40}}
          (text
            {:style {:fontWeight "normal" :color "white" :fontSize 24 :marginBottom 10}}
            "ERROR")
          (text
            {:style {:color "white" :fontFamily "Menlo-Regular" :fontSize 16 :lineHeight 24}}
            (.-message e))
          (text
            {:style {:color "white" :marginTop 20 :fontSize 16 :fontWeight "bold"}}
            "Check REPL log for details."))))))


(defmulti read om/dispatch)
(defmethod read :default
  [{:keys [state]} k _]
  (let [st @state]
    (if-let [[_ v] (find st k)]
      {:value v}
      {:value :not-found})))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser (om/parser {:read read})
    :root-render #(.render js/React %1 %2)
    :root-unmount #(.unmountComponentAtNode js/React %)}))

(om/add-root! reconciler WidgetComponent 1)
