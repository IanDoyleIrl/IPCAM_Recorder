dojo.provide("core.cameraWidget");

//Create our widget!
dojo.declare('core.cameraWidget', [dijit._Widget, dijit._Templated, dijit._WidgetsInTemplateMixin],{
    widgetsInTemplate : true,
    templatePath: "js/htmlTemplates/cameraWidget.html",
    cameras : null,
    _this : null,

    postCreate : function(){
        //alert("camera");
        _this = this;
        this.cameraDetailsDialog.hide();
        this.populateVariables();
        this.loadCameraDiv();
        dojo.on(this.addCameraButton, "click", dojo._base.lang.hitch(_this, "handleCameraUpdateOrAdd" ,null))
        dojo.on(this.okButton, "click", dojo._base.lang.hitch(this, "handleCameraFormPost"));
    },

    loadCameraDiv : function(){
        var table = dojo.byId("cameraTable");
        for(var i=0; i<table.rows.length; i++){
            table.deleteRow(i);
        }
        for(var i=0; i<this.cameras.length; i++){
            this.addRow(this.cameras[i]);
        }
    },

    populateVariables : function (){
        var cameraArgs = {
            url: "/api/camera",
            handleAs: "json",
            sync: true,
            load: function(data){
                _this.cameras = data;
            },
            error: function(error){
                //alert(error);
            }
        }
        // Call the asynchronous xhrGet
        dojo.xhrGet(cameraArgs);
    },

    sendCameraPost :function(camera){
        if (camera.id != null || camera.id != "" || camera.id != undefined){
                var xhrArgs = {
                    url: "/api/camera",
                    postData: dojo.toJson(camera),
                    handleAs: "json",
                    headers: { "Content-Type": "application/json"},
                    load: dojo._base.lang.hitch(this, function(data){
                        this.cameras = data;
                        this.loadCameraDiv();
                        this.cameraDetailsDialog.hide();
                    }),
                    error: function(error){
                        //alert(error)
                    }
                }
                var deferred = dojo.rawXhrPost(xhrArgs);
        }
        else{
                var xhrArgs = {
                    url: "/api/camera/" + camera.id,
                    postData: camera,
                    handleAs: "json",
                    load: dojo._base.lang.hitch(this, function(data){
                        this.cameras = data;
                        this.loadCameraDiv();
                    }),
                    error: function(error){
                       // alert(error);
                    }
                }
                var deferred = dojo.xhrPost(xhrArgs);
            }
    },

    handleCameraFormPost : function(){
        var _id = dojo.byId("idInput").value;
        var _name = dojo.byId("nameInput").value;
        var _url = dojo.byId("urlInput").value;
        var _active = dojo.byId("activeInput").checked;
        var camera = {
            id : _id,
            name : _name,
            url : _url,
            active : _active
        }
        dojo._base.lang.hitch(this, this.sendCameraPost(camera));
    },

    handleActiveCameraUpdate : function(camera){
        this.sendCameraPost(camera);
    },

    handleCameraUpdateOrAdd : function(camera){
        var id = dojo.byId("idInput");
        var name = dojo.byId("nameInput");
        var url = dojo.byId("urlInput");
        var active = dojo.byId("activeInput");
        if (camera == null){
            id.value = null;
            name.value = "";
            url.value = "";
            active.value = false;
            this.cameraDetailsDialog.set("title", "New Camera")
        }
        else{
            id.value = camera.id;
            name.value = camera.name;
            url.value = camera.url;
            active.value = camera.active;
            this.cameraDetailsDialog.set("title", "Edit Camera")
        }
        this.cameraDetailsDialog.show();
        return;
    },

    createCheckbox : function(camera, checkboxTD){
        var checkBox = new dijit.form.CheckBox({
            checked: camera.active
        }, checkboxTD);
        dojo.on(checkBox, "change", dojo._base.lang.hitch(_this, "handleActiveCameraUpdate", camera))
    },

    createButton : function(camera, buttonTD){
        var myButton = new dijit.form.Button({
            label: "Edit",
            onClick: dojo.hitch(_this, "handleCameraUpdateOrAdd", camera)
        }, buttonTD);
        //domStyle.set(checkBox, "width", "30%");
    },

    addRow : function(camera) {
        var table = dojo.byId("cameraTable");
        var row = table.insertRow(table.rows.length);
        var id = row.insertCell(0);
        var name = row.insertCell(1);
        var status = row.insertCell(2);
        var edit = row.insertCell(3);
        id.innerHTML = camera.id;
        name.innerHTML = camera.name;
        this.createCheckbox(camera, status);
        this.createButton(camera, edit);
    }


});