
dojo.provide("core.streamingWidget");

//Create our widget!
dojo.declare('core.streamingWidget', [dijit._Widget, dijit._Templated],{
    widgetsInTemplate : true,
    templatePath: "js/htmlTemplates/streamingWidget.html",
    _this : null,

    postCreate : function(){
        _this = this;
        var imgNode = dojo.byId("streamDiv")
        imgNode.src = "/Videostream?mode=live&cameraId=1";
    }


});