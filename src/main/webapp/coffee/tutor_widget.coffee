class WidgetBase
  constructor: (@name) ->

  alive: ->
    false

class WidgetInteger extends WidgetBase
  constructor: ->
    super("Integer")

  dead: ->
    not @alive()

window.widgets = {}
window.widgets.WidgetBase = WidgetBase
window.widgets.WidgetInteger = WidgetInteger


