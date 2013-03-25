
dojo.provide("core.streamingWidget");

dojo.require("dijit.form.Select");
dojo.require("dijit.form.TextBox");
dojo.require("dojox.layout.TableContainer");
dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dojox.data.CsvStore");
dojo.require("dojox.grid.EnhancedGrid");
dojo.require("dojo.data.ItemFileReadStore");
dojo.require("dojox.layout.TableContainer");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.NumberSpinner");
dojo.require("dojox.grid.enhanced.plugins.NestedSorting");
dojo.require("dojox.grid.enhanced.plugins.IndirectSelection");
dojo.require("dijit.Menu");
dojo.require("dijit.MenuItem");

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