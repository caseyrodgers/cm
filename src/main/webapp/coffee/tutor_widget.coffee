

class HmWidget
  constructor: (@jsonObj) ->

  initializeWidget: ->
    alert "base initializeWidget"

class WidgetInteger extends HmWidget
  constructor: ->
    super("{}")

  initializeWidget: ->
    alert "initializeWidget in Integer"
    super.initializeWidget
    
    


# how to register each class for Factory creation?

window.widgets = {}
window.widgets.HmWidget = HmWidget
window.widgets.WidgetInteger = WidgetInteger


