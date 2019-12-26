/**
 * Copyright (c) 2006
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
if(!ORYX){ var ORYX = {} }
if(!ORYX.Plugins){ ORYX.Plugins = {} }

ORYX.Plugins.ApromoreSave = Clazz.extend({

    facade:undefined,

    changeSymbol:"*",

    construct:function (facade) {
        this.facade = facade;

        this.facade.offer({
            'name':ORYX.I18N.Save.save,
            'functionality':this.save.bind(this, false),
            'group':ORYX.I18N.Save.group,
            'icon':ORYX.PATH + "images/disk.png",
            'description':ORYX.I18N.Save.saveDesc,
            'index':1,
            'minShape':0,
            'maxShape':0,
            keyCodes:[
                {
                    metaKeys:[ORYX.CONFIG.META_KEY_META_CTRL],
                    keyCode:83, // s-Keycode
                    keyAction:ORYX.CONFIG.KEY_ACTION_UP
                }
            ]
        });

        document.addEventListener("keydown", function (e) {
            if (e.ctrlKey && e.keyCode === 83) {
                Event.stop(e);
            }
        }, false);


        this.facade.offer({
            'name':ORYX.I18N.Save.saveAs,
            'functionality':this.save.bind(this, true),
            'group':ORYX.I18N.Save.group,
            'icon':ORYX.PATH + "images/disk_multi.png",
            'description':ORYX.I18N.Save.saveAsDesc,
            'index':2,
            'minShape':0,
            'maxShape':0
        });

        window.onbeforeunload = this.onUnLoad.bind(this);
        this.changeDifference = 0;

        // Register on event for executing commands --> store all commands in a stack
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_UNDO_EXECUTE, function () {
            this.changeDifference++;
            this.updateTitle();
        }.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_EXECUTE_COMMANDS, function () {
            this.changeDifference++;
            this.updateTitle();
        }.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_UNDO_ROLLBACK, function () {
            this.changeDifference--;
            this.updateTitle();
        }.bind(this));

    },

    updateTitle:function () {
        var value = window.document.title;
        var docElement = document.getElementsByTagName("title")[0];
        if (docElement) {
            var child = docElement.childNodes[0];
            if (child) {
                value = docElement.nodeValue;
            }
        }
        if (value) {
            if (this.changeDifference === 0 && value.startsWith(this.changeSymbol)) {
                window.document.title = value.slice(1);
            } else if (this.changeDifference !== 0 && !value.startsWith(this.changeSymbol)) {
                window.document.title = this.changeSymbol + "" + value;
            }
        }
    },

    onUnLoad:function () {
        if (this.changeDifference !== 0 || (this.facade.getModelMetaData()['new'] && this.facade.getCanvas().getChildShapes().size() > 0)) {
            return ORYX.I18N.Save.unsavedData;
        }
    },

    getSVG:function () {
        var selection = this.facade.getSelection();
        this.facade.setSelection([]);

        // Get the serialized svg image source
        var svgClone = this.facade.getCanvas().getSVGRepresentation(true);
        this.facade.setSelection(selection);
        if (this.facade.getCanvas().properties["oryx-showstripableelements"] === false) {
            var stripOutArray = svgClone.getElementsByClassName("stripable-element");
            for (var i = stripOutArray.length - 1; i >= 0; i--) {
                stripOutArray[i].parentNode.removeChild(stripOutArray[i]);
            }
        }

        // Remove all forced stripable elements
        var stripOutArray = svgClone.getElementsByClassName("stripable-element-force");
        for (var i = stripOutArray.length - 1; i >= 0; i--) {
            stripOutArray[i].parentNode.removeChild(stripOutArray[i]);
        }

        // Parse dom to string
        return DataManager.serialize(svgClone);
    },

    /**
     * Saves the current process to the server.
     */
    save:function (forceNew, event) {
        if (this.saving) {
            return false;
        }

        this.saving = true;
        this.facade.raiseEvent({
            type:ORYX.CONFIG.EVENT_ABOUT_TO_SAVE
        });

        var json = Ext.encode(this.facade.getJSON());
        var svg = this.getSVG();

        if (forceNew) {
            if (ORYX.Plugins.ApromoreSave.apromoreSaveAs) {
                ORYX.Plugins.ApromoreSave.apromoreSaveAs(json, svg);
            } else {
                alert("Apromore Save As method is missing!");
            }
        } else {
            if (ORYX.Plugins.ApromoreSave.apromoreSave) {
                ORYX.Plugins.ApromoreSave.apromoreSave(json, svg);
            } else {
                alert("Apromore Save method is missing!");
            }
        }

        this.saving = false;
        return true;
    }

});

function getObjectClass(obj) {
    if (obj && obj.constructor && obj.constructor.toString) {
        var arr = obj.constructor.toString().match(/function\s*(\w+)/);

        if (arr && arr.length == 2) {
            return arr[1];
        }
    }

    return undefined;
}

