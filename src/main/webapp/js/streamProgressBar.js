
dojo.provide("core.streamProgressBar");

//Create our widget!
dojo.declare('core.streamProgressBar', [ dijit.ProgressBar, dijit._Widget],{

    stream : null,

    constructor : function(){
        console.log("Test");
    },

    postCreate : function(){
        this.connect(this.domNode, "onclick", "_handleClick");
    },

    _handleClick : function(evt){
        var totalWidth = evt.currentTarget.clientWidth;
        var currentSelection = evt.layerX;
        var percentageSelected = dojo.number.format((currentSelection / totalWidth) * 100, {
            places: 2
        });
        this.stream.jumpTo({
            action : "jumpTo",
            type : "percent",
            value : percentageSelected
        })
    }


});