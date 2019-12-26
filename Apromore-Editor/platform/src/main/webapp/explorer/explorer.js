/**
 * Copyright (c) 2009 - Signavio GmbH
 *
 *
 *
 *
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 *
 **/
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Core) {
    Signavio.Core = {}
}
Signavio.Core.Version = "4.1.4";
XMLNS = {ATOM: "http://www.w3.org/2005/Atom", XHTML: "http://www.w3.org/1999/xhtml", ERDF: "http://purl.org/NET/erdf/profile", RDFS: "http://www.w3.org/2000/01/rdf-schema#", RDF: "http://www.w3.org/1999/02/22-rdf-syntax-ns#", RAZIEL: "http://b3mn.org/Raziel", SCHEMA: ""};
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Config) {
    Signavio.Config = {}
}
if (!Signavio.Const) {
    Signavio.Const = {}
}
if (!Signavio.Const.REL) {
    Signavio.Const.REL = {}
}
Signavio.Config.EXPLORER_PATH = "/explorer";
Signavio.Config.APPLICATION_NAME = "Explorer";
Signavio.Config.EDITOR_PATH = "/editor";
Signavio.Config.DIAGRAMS_IMAGE_PATH = Signavio.Config.EXPLORER_PATH + "/src/img";
Signavio.Config.STENCILSET_EXTENSION_PATH = Signavio.Config.EDITOR_PATH + "/stencilsets/extensions/";
Signavio.Config.SSEXTENSIONS_PATH = "/editor_ssextensions";
Signavio.Config.BACKEND_PATH = "/p";
Signavio.Config.LIBS_PATH = "/libs";
Signavio.Config.PATH = Signavio.Config.EXPLORER_PATH + "/src/javascript";
Signavio.Config.PLUGIN_PATH = Signavio.Config.EXPLORER_PATH + "/src/javascript/plugins";
Signavio.Config.EDITOR_HANDLER_URI = Signavio.Config.BACKEND_PATH + "/editor";
Signavio.Config.SPREADSHEET_EDITOR_HANDLER_URI = Signavio.Config.BACKEND_PATH + "/spreadsheet";
Signavio.Config.STENCILSET_URI = Signavio.Config.BACKEND_PATH + "/editor_stencilset";
Signavio.Config.PLUGIN_CONFIG = Signavio.Config.BACKEND_PATH + "/explorer_plugins";
Signavio.Config.GLOSSARY_URI = Signavio.Config.BACKEND_PATH + "/glossary";
Signavio.Config.PUBLISHER_URI = Signavio.Config.BACKEND_PATH + "/publisher";
Signavio.Config.MASHUP_URI = "/mashup";
Signavio.Config.HELP_START_URL = "/help/en/index.html";
Signavio.Config.HELP_START_URL_DE = "/help/de/index.html";
Signavio.Config.HELP_PDF_URL = "/help/en/SignavioProcessEditor.pdf";
Signavio.Config.HELP_PDF_URL_DE = "/help/de/SignavioProcessEditor_de.pdf";
Signavio.Config.GETTING_STARTET_URL = "/help/screencasts/screencasts_EN.html";
Signavio.Config.GETTING_STARTET_URL_DE = "/help/screencasts/screencasts_DE.html";
Signavio.Config.SHOW_LOGIN_LOGOUT = true;
Signavio.Config.SHOW_LICENCE = true;
Signavio.Config.TEACHING_PACKAGE = "http://bpt.hpi.uni-potsdam.de/SignavioOryxAcademicInitiative/WebHome";
Signavio.Config.LOGOUT_HANDLER = Signavio.Config.BACKEND_PATH + "/login?logout=true";
Signavio.Config.LOGIN_PAGE = "/p/login";
Signavio.Config.RESET_PASSWORD_PAGE = "/p/resetPassword";
Signavio.Config.VALIDATE_ORDER_PATH = "/p/validate_order";
Signavio.Config.MIGRATION_PATH = "/p/migration";
Signavio.Config.CONFIG_PATH = "/config";
Signavio.Config.SEARCH_PATH = "/search";
Signavio.Config.INVITATION_PATH = "/invitation";
Signavio.Config.MODEL_PATH = "/model";
Signavio.Config.DIRECTORY_PATH = "/directory";
Signavio.Config.USER_PATH = "/user";
Signavio.Config.USERGROUP_PATH = "/usergroup";
Signavio.Config.TENANT_PATH = "/tenant";
Signavio.Config.ORDER_PATH = "/order";
Signavio.Config.PRIVATE_URL_PATH = "/purl";
Signavio.Config.ARCHIVE_EXPORT_PATH = "/zip-export";
Signavio.Config.ARCHIVE_IMPORT_PATH = "/zip-import";
Signavio.Config.ACCESS_PATH = "/access";
Signavio.Config.INFO_PATH = "/info";
Signavio.Config.TALKABOUT_PATH = "/talkabout";
Signavio.Config.VIEWS_PATH = "/views";
Signavio.Config.AML_IMPORT_PATH = "/aml-upload";
Signavio.Config.RESPONSIBILITY_ANALYSIS_PATH = "/responsibility-analysis";
Signavio.Config.XPDL_IMPORT_PATH = "/xpdl";
Signavio.Config.BPMN_INFO_PATH = "/bpmninfo";
Signavio.Config.VISIO_IMPORT_PATH = "/visio";
Signavio.Config.PROCESS_DOCU_PATH = "/docu";
Signavio.Config.PRINT_MULTIPLE_PDFS_PATH = "/printsvg";
Signavio.Config.MAIL_PATH = "/mail";
Signavio.Config.ADMINISTRATOR_GROUP = "Administrators";
Signavio.Config.FOLDER_TYPE = {};
Signavio.Config.FOLDER_TYPE.PRIVATE = "private";
Signavio.Config.FOLDER_TYPE.PUBLIC = "public";
Signavio.Config.FOLDER_TYPE.TRASH = "trash";
Signavio.Config.GLOSSARY_ENABLED = true;
Signavio.Config.USE_CACHE = true;
Signavio.Config.LOGLEVEL = 7;
Signavio.Config.LOGLEVELSHOWN = 3;
Signavio.Config.WARNING_SHOWN = 5000;
Signavio.Config.INITIAL_VIEW = "icon";
Signavio.Config.SORT_DESC = "desc";
Signavio.Config.SORT_ASC = "asc";
Signavio.Config.TRIAL_LICENSE = "trial";
Signavio.Config.TEAM_LICENSE = "team";
Signavio.Config.PREMIUM_LICENSE = "premium";
Signavio.Config.ACCESS_RIGHTS = ["warehouse.read", "warehouse.delete", "warehouse.share", "warehouse.write", "all"];
if (!Signavio.Config.RIGHTS) {
    Signavio.Config.RIGHTS = {}
}
Signavio.Config.RIGHTS.ALL = "all";
Signavio.Config.RIGHTS.READ = "warehouse.read";
Signavio.Config.RIGHTS.MODIFY = "warehouse.modify";
Signavio.Config.RIGHTS.DELETE = "warehouse.delete";
Signavio.Config.RIGHTS.SHARE = "warehouse.share";
Signavio.Config.RIGHTS.WRITE = "warehouse.write";
Signavio.Config.RIGHTS.GROUP_DELETE = "usermanagement.usergroup.delete";
Signavio.Config.RIGHTS.GROUP_REMOVE = "usermanagement.usergroup.remove";
Signavio.Config.RIGHTS.GROUP_ADD = "usermanagement.usergroup.add";
Signavio.Config.RIGHTS.GROUP_CHANGE = "usermanagement.usergroup.change";
Signavio.Config.RIGHTS.USER_CHANGE = "usermanagement.user.change";
Signavio.Config.RIGHTS.USER_DELETE = "usermanagement.deleteuser";
Signavio.Config.RIGHTS.GLOSSARY_OPEN = "glossary.open";
Signavio.Config.RIGHTS.GLOSSARY_WRITE = "glossary.write";
Signavio.Config.RIGHTS.GLOSSARY_SHARE = "glossary.share";
Signavio.Config.SEARCH_LIMIT = 20;
Signavio.Config.REMOVE_ON_DELETE = false;
Signavio.Config.HIDE_NOTIFICATION = false;
Signavio.Const.PUBLIC_USER = "public";
Signavio.Const.DATE_FORMAT = "d.m.Y H:i";
Signavio.Const.MAIL_VALIDATOR = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,4}$";
Signavio.Const.EVENT = {};
Signavio.Const.EVENT.ROOTSELECTIONCHANGE = "rootselectionchange";
Signavio.Const.EVENT.FOLDER_SELECTION_CHANGE = "folderselectionchange";
Signavio.Const.EVENT.SHOW_INFO_IMG = "showinfoimg";
Signavio.Const.EVENT.SHOW_TALK_ABOUT = "showTalkAbout";
Signavio.Const.EVENT.USER_LOADED = "userloaded";
Signavio.Const.EVENT.SEARCH_ACTIVATED = "searchactivated";
Signavio.Const.EVENT.SEARCH_DEACTIVATED = "searchdeactivated";
Signavio.Const.EVENT.REVISION_SELECTION_CHANGED = "revisionchanged";
Signavio.Const.REL.DIRECTORY = "dir";
Signavio.Const.REL.MODEL = "mod";
Signavio.Const.REL.INFO = "info";
Signavio.Const.REL.USER = "user";
Signavio.Const.REL.ORDER = "order";
Signavio.Const.REL.PURL = "purl";
Signavio.Const.REL.NOTIFY = "notify";
Signavio.Const.REL.PRIVILEGE = "priv";
Signavio.Const.REL.GROUP = "group";
Signavio.Const.REL.CHILD_GROUP = "cgroup";
Signavio.Const.REL.GLOSSARY = "glos";
Signavio.Const.REL.INVITATION = "invitation";
Signavio.Const.REL.VIEWS = "views";
Signavio.Const.NAMESPACE = {};
Signavio.Const.NAMESPACE.BPMN1_1 = "http://b3mn.org/stencilset/bpmn1.1#";
Signavio.Const.NAMESPACE.BPMN1_2 = Signavio.Const.NAMESPACE.BPMN1_1;
if (!Signavio.Const.PURL) {
    Signavio.Const.PURL = {}
}
Signavio.Const.PURL.PNG = "png";
Signavio.Const.PURL.DISCUSSION = "talkabout";
Signavio.Const.PURL.JSON = "json";
Signavio.Const.PURL.STENCILSET = "stencilset";
Signavio.Const.PURL.GADGET = "gadget";
Signavio.Const.THUMBNAIL = "/thumbnail";
if (!Signavio.Const.ACCESS_TYPES) {
    Signavio.Const.ACCESS_TYPES = {}
}
Signavio.Const.ACCESS_TYPES.mod = ["warehouse.read", "warehouse.write", "warehouse.delete", "warehouse.share"];
Signavio.Const.ACCESS_TYPES.dir = ["warehouse.read", "warehouse.write", "warehouse.delete", "warehouse.share"];
Signavio.Const.ACCESS_TYPES.glos = ["glossary.open", "glossary.write"];
Signavio.Const.FRAGMENTS_ENABLED = false;
if (!Signavio.Const.META_TYPES) {
    Signavio.Const.META_TYPES = {}
}
Signavio.Const.META_TYPES.GLOSSARY = [
    {type: "MetaDataStringInfo", asList: true},
    "MetaDataStringInfoMulti",
    {type: "MetaDataGlossaryLink", asList: true},
    {type: "MetaDataUrl", asList: true},
    "MetaDataBoolean"
];
Signavio.Const.META_TYPES.STENCILSET = [
    {type: "MetaDataStringInfo", asList: true},
    "MetaDataStringInfoMulti",
    {type: "MetaDataGlossaryLink", asList: true},
    "MetaDataDate",
    "MetaDataNumberInfo",
    "MetaDataEnum",
    {type: "MetaDataUrl", asList: true},
    "MetaDataBoolean"
];
Signavio.Config.HEADER_LOGO_LINK_TARGET = "http://www.signavio.com";
Ext.BLANK_IMAGE_URL = ".." + Signavio.Config.LIBS_PATH + "/ext-2.0.2/resources/images/default/s.gif";
Ext.Rating = Ext.extend(Ext.BoxComponent, {maxScore: 5, minScore: 1, scoreImgEnabled: "../images/silk/star.png", scoreImgDisabled: "../images/silk/star_gray.png", imgStyle: "width:12px;margin:2px;", imgEditStyle: "cursor:pointer;", textStyle: "margin-left:5px;position:relative;top:-4px;", disabledImg: "../images/silk/bullet_black.png", value: 0, text: "", stars: [], editable: false, _setStyle: function (b, a) {
    if (Ext.isIE) {
        b.style.setAttribute("cssText", a)
    } else {
        b.setAttribute("style", a)
    }
}, onRender: function (e, a) {
    if (!this.el) {
        this.el = document.createElement("div");
        this.el.id = this.getId();
        this.stars = [];
        for (var d = this.minScore; d <= this.maxScore; d++) {
            var b = document.createElement("img");
            if (this.editable) {
                Element.observe(b, "mouseover", this.onMouseOverStar.bind(this), true);
                Element.observe(b, "mouseout", this.onMouseOutStar.bind(this), true);
                Element.observe(b, "click", this.onClickStar.bind(this), true);
                this._setStyle(b, this.imgStyle + "" + this.imgEditStyle)
            } else {
                this._setStyle(b, this.imgStyle)
            }
            this.stars.push(b)
        }
        this.stars.each(function (f) {
            this.el.appendChild(f)
        }.bind(this));
        this.textEl = document.createElement("span");
        this._setStyle(this.textEl, this.textStyle);
        this.textEl.innerHTML = this.text;
        this.el.appendChild(this.textEl);
        if (this.forId) {
            this.el.setAttribute("htmlFor", this.forId)
        }
    }
    this.setStars();
    if (e) {
        Ext.Rating.superclass.onRender.call(this, e, a)
    }
}, setStars: function (b) {
    if (!b) {
        b = this.value
    }
    var a = this.disabled || b <= 0;
    this.stars.each(function (g, d) {
        var h = d + this.minScore;
        var f = h <= (b + 0.5);
        var e = f ? this.scoreImgEnabled : this.scoreImgDisabled;
        e = a ? this.disabledImg : e;
        g.setAttribute("src", e);
        g.setAttribute("title", h)
    }.bind(this))
}, onMouseOverStar: function (a) {
    this.setStars(this.stars.indexOf(a.target) + this.minScore)
}, onMouseOutStar: function (a) {
    this.setStars()
}, onClickStar: function (b) {
    var a = this.stars.indexOf(b.target) + this.minScore;
    this.setValue(a);
    if (this.changed instanceof Function) {
        this.changed(a)
    }
}, setValue: function (a) {
    this.value = a;
    this.setStars()
}, setText: function (a) {
    this.text = a;
    if (this.textEl) {
        this.textEl.innerHTML = a
    }
}, startEdit: function () {
    this._setStyle(this.el.dom, "left:22px;position:absolute;top:2px;")
}, completeEdit: function () {
}, cancelEdit: function () {
}});
Ext.reg("rating", Ext.Rating);
Ext.LinkButton = Ext.extend(Ext.BoxComponent, {click: null, image: null, imageStyle: null, toggle: false, toggleStyle: null, selected: false, href: undefined, el: null, onRender: function (b, a) {
    if (this.el == null) {
        this.el = document.createElement("a");
        if (this.tabIndex) {
            this.el.setAttribute("tabindex", this.tabIndex)
        }
        this.el.id = this.getId();
        this.el.className = this.cls || "x-link-button";
        this.el.href = this.href !== undefined ? this.href : "#" + this.text;
        Element.observe(this.el, "click", this.onClick.bind(this));
        if (this.image) {
            this.el.innerHTML = '<img src="' + this.image + '" title="' + this.text + '"' + (this.imageStyle ? ' style="' + this.imageStyle + '"/>' : "/>")
        } else {
            this.el.innerHTML = this.text ? Ext.util.Format.htmlEncode(this.text) : (this.html || "")
        }
        if (this.forId) {
            this.el.setAttribute("htmlFor", this.forId)
        }
    }
    Ext.LinkButton.superclass.onRender.call(this, b, a)
}, onClick: function (a) {
    Event.stop(a);
    if (this.disabled) {
        return
    }
    if (this.toggle) {
        this.selected = !this.selected;
        if (this.toggleStyle) {
            this._setStyle(this.el.dom, "");
            this.el.dom.setAttribute("style", "");
            if (this.selected) {
                this.el.applyStyles(this.toggleStyle)
            } else {
                this.el.applyStyles(this.initialConfig.style)
            }
        }
    }
    if (this.click instanceof Function) {
        this.click.apply(this.click, [this, a])
    }
}, setText: function (a, b) {
    this.text = a;
    if (this.rendered) {
        this.el.dom.innerHTML = b !== false ? Ext.util.Format.htmlEncode(a) : a
    }
    return this
}, _setStyle: function (b, a) {
    if (Ext.isIE) {
        b.style.setAttribute("cssText", a)
    } else {
        b.setAttribute("style", a)
    }
}});
Ext.reg("linkbutton", Ext.LinkButton);
Ext.SimpleButton = Ext.extend(Ext.Button, {template: new Ext.Template("<a href='#' class='x-button-plain'>{0}</a>"), handler: Ext.emptyFn, onRender: function () {
    this.el = Ext.get(this.template.overwrite(this.container.dom, [this.text]));
    this.el.on("click", function (b, a) {
        this.handler.apply(this, arguments);
        b.stopEvent();
        return false
    }.bind(this))
}});
Ext.form.ComboBoxMulti = function (a) {
    Ext.apply(a);
    this.typeAhead = false;
    this.minChars = 1;
    this.validationEvent = true;
    this.hideTrigger = true;
    this.defaultAutoCreate = {tag: a.renderAsTextArea ? "textarea" : "input", autocomplete: "off"};
    Ext.form.ComboBoxMulti.superclass.constructor.call(this, a)
};
Ext.form.ComboBoxMulti = Ext.extend(Ext.form.ComboBoxMulti, Ext.form.ComboBox, {anyMatch: false, getPosition: function () {
    if (document.selection) {
        var a = document.selection.createRange();
        var b = a.duplicate();
        b.moveToElementText(this.el.dom);
        b.setEndPoint("EndToEnd", a);
        return b.text.length
    } else {
        return this.el.dom.selectionEnd
    }
}, getActiveRange: function () {
    var b = this.sep;
    var f = this.getPosition();
    var a = this.getRawValue();
    var g = a.split(this.sep);
    var e = f;
    while (e > 0 && (a.charAt(e) != b)) {
        --e
    }
    if (e > 0) {
        e++
    }
    return{left: e, right: f}
}, getActiveEntry: function () {
    var a = this.getActiveRange();
    return this.getRawValue().substring(a.left, a.right).replace(/^\s+|\s+$/g, "")
}, replaceActiveEntry: function (d) {
    var b = this.getActiveRange();
    var a = this.getRawValue();
    if (this.preventDuplicates && a.indexOf(d) >= 0) {
        return
    }
    var f = (this.sep == " " ? "" : " ");
    f = (this.sep == "\n" ? "" : f);
    this.setValue(a.substring(0, b.left) + (b.left > 0 ? f : "") + d + this.sep + f + a.substring(b.right));
    var e = b.left + d.length + 2 + f.length;
    this.selectText.defer(200, this, [e, e])
}, onSelect: function (a, b) {
    if (this.fireEvent("beforeselect", this, a, b) !== false) {
        var d = a.data[this.valueField || this.displayField];
        if (this.sep) {
            this.replaceActiveEntry(d)
        } else {
            this.setValue(d)
        }
        this.collapse();
        this.fireEvent("select", this, a, b)
    }
}, initEvents: function () {
    Ext.form.ComboBoxMulti.superclass.initEvents.call(this);
    this.keyNav.doRelay = function (d, b, a) {
        if (this.scope.isExpanded()) {
            return Ext.KeyNav.prototype.doRelay.apply(this, arguments)
        }
        return true
    }
}, initQuery: function () {
    this.doQuery(this.sep ? this.getActiveEntry() : this.getRawValue())
}, selectText: function (g, a) {
    var e = this.sep ? this.getActiveEntry() : this.getRawValue();
    if (e.length > 0) {
        g = g === undefined ? this.getRawValue().indexOf(e) : g;
        a = a === undefined ? e.length : a;
        var f = this.el.dom;
        if (f.setSelectionRange) {
            f.setSelectionRange(g, a)
        } else {
            if (f.createTextRange) {
                var b = f.createTextRange();
                b.moveStart("character", g);
                b.moveEnd("character", a - e.length);
                b.select()
            }
        }
    }
}, onLoad: function () {
    if (!this.hasFocus) {
        return
    }
    if (this.store.getCount() > 0) {
        this.expand();
        this.restrictHeight();
        if (this.lastQuery == this.allQuery) {
            if (this.editable) {
                this.selectText()
            }
            if (!this.selectByValue(this.value, true)) {
                this.select(0, true)
            }
        } else {
            this.selectNext();
            if (this.typeAhead && this.lastKey != Ext.EventObject.BACKSPACE && this.lastKey != Ext.EventObject.DELETE) {
                this.taTask.delay(this.typeAheadDelay)
            }
        }
    } else {
        this.onEmptyResults()
    }
}, growMin: 60, growMax: 1000, onKeyUp: function (a) {
    if (!a.isNavKeyPress() || a.getKey() == a.ENTER) {
        Ext.form.ComboBoxMulti.superclass.onKeyUp.call(this, a);
        this.autoSize()
    }
}, doQuery: function (d, b) {
    if (d === undefined || d === null) {
        d = ""
    }
    var a = {query: d, forceAll: b, combo: this, cancel: false};
    if (this.fireEvent("beforequery", a) === false || a.cancel) {
        return false
    }
    d = a.query;
    b = a.forceAll;
    if (b === true || (d.length >= this.minChars)) {
        if (this.lastQuery !== d) {
            this.lastQuery = d;
            if (this.mode == "local") {
                this.selectedIndex = -1;
                if (b) {
                    this.store.clearFilter()
                } else {
                    this.store.filter(this.displayField, d, this.anyMatch)
                }
                this.onLoad()
            } else {
                this.store.baseParams[this.queryParam] = d;
                this.store.load({params: this.getParams(d)});
                this.expand()
            }
        } else {
            this.selectedIndex = -1;
            this.onLoad()
        }
    }
}, autoSize: function () {
    if (!this.grow || this.defaultAutoCreate.tag !== "textarea" || !this.el) {
        return
    }
    var e = this.el;
    var a = e.dom.value;
    var d = e.getStyle("line-height") == "normal" ? 14.4 : e.getStyle("line-height").replace("px", "");
    var b = Number(d) * (a.split("\n").length + 1);
    b -= 6;
    b += e.isScrollable() ? 20 : 0;
    b = Math.min(this.growMax, Math.max(b, this.growMin));
    if (b != this.lastHeight) {
        this.lastHeight = b;
        this.el.setHeight(b)
    }
}});
Ext.reg("combomulti", Ext.form.ComboBoxMulti);
Ext.form.DataStoreComboBox = Ext.extend(Ext.form.ComboBox, {typeAhead: true, mode: "local", minChars: 0, triggerAction: "all", selectOnFocus: true, setValue: function (a) {
    var d = a;
    if (this.store) {
        var b = this.store.find(this.valueField, a);
        if (b >= 0) {
            b = this.store.getAt(b);
            d = this.store.parseValue(b, this.displayField);
            a = b
        }
        this.lastSelectionText = d;
        Ext.form.DataStoreComboBox.superclass.setValue.call(this, d)
    }
    this.value = a
}, selectByValue: function (b, f) {
    if (b !== undefined && b !== null && typeof b === "string") {
        var e = this.findRecord(this.valueField || this.displayField, b);
        if (!e) {
            var d = this.store;
            var a = this.displayField;
            e = d.data.items.find(function (g) {
                return d.parseValue(g, a).toLowerCase().startsWith(b.toLowerCase())
            });
            if (!e) {
                e = d.data.items.find(function (g) {
                    return d.parseValue(g, a).toLowerCase().include(b.toLowerCase())
                })
            }
        }
        if (e) {
            this.select(this.store.indexOf(e), f);
            return true
        }
    }
    return false
}, onSelect: function () {
    Ext.form.DataStoreComboBox.superclass.onSelect.apply(this, arguments);
    var a = this.getRawValue().length;
    this.selectText(a, a)
}, doQuery: function (d, b) {
    if (d === undefined || d === null) {
        d = ""
    }
    var a = {query: d, forceAll: b, combo: this, cancel: false};
    if (this.fireEvent("beforequery", a) === false || a.cancel) {
        return false
    }
    d = a.query;
    b = a.forceAll;
    if (b === true || (d.length >= this.minChars)) {
        if (this.lastQuery !== d) {
            this.lastQuery = d;
            if (this.mode == "local") {
                this.selectedIndex = -1;
                if (b) {
                    this.store.clearFilter()
                } else {
                    this.store.filter(this.displayField || this.valueField, d)
                }
                this.onLoad()
            } else {
                this.store.baseParams[this.queryParam] = d;
                this.store.load({params: this.getParams(d)});
                this.expand()
            }
        } else {
            this.selectedIndex = -1;
            this.onLoad()
        }
    }
}, onLoad: function () {
    if (!this.hasFocus) {
        return
    }
    if (this.store.getCount() > 0) {
        this.expand();
        this.restrictHeight();
        if (this.lastQuery == this.allQuery) {
            if (this.editable) {
                this.el.dom.select()
            }
            if (!this.selectByValue(this.value, true)) {
                this.select(0, true)
            }
        } else {
            var a = this.getRawValue();
            if (a) {
                var b = this.store.data.items.find(function (d) {
                    return this.store.parseValue(d, this.displayField).toLowerCase().startsWith(a.toLowerCase())
                }.bind(this));
                if (!b) {
                    b = this.store.data.items.find(function (d) {
                        return this.store.parseValue(d, this.displayField).toLowerCase().include(a.toLowerCase())
                    }.bind(this))
                }
            }
            if (b) {
                this.select(this.store.indexOf(b), true)
            } else {
                this.selectNext()
            }
            if (this.typeAhead && this.lastKey != Ext.EventObject.BACKSPACE && this.lastKey != Ext.EventObject.DELETE) {
                this.taTask.delay(this.typeAheadDelay)
            }
        }
    } else {
        this.onEmptyResults()
    }
}, onTypeAhead: function () {
    if (this.store.getCount() > 0) {
        var f = this.getRawValue().toLowerCase();
        var b = this.store.data.items.find(function (g) {
            return g.get("rep").name.toLowerCase().startsWith(f)
        }) || this.store.getAt(0);
        var d = this.store.parseValue(b, this.displayField);
        var a = d.length;
        var e = this.getRawValue().length;
        if (e != a) {
            this.setRawValue(d.unescapeHTML());
            this.selectText(e, d.length)
        }
    }
}});
Ext.DataView.LabelEditor = function (a, b) {
    Ext.DataView.LabelEditor.superclass.constructor.call(this, b || new Ext.form[a.tag === "textarea" ? "TextArea" : "TextField"]({allowBlank: false, grow: a.tag === "textarea" && a.grow !== false}), a)
};
Ext.extend(Ext.DataView.LabelEditor, Ext.Editor, {alignment: "tl-tl", hideEl: false, cls: "x-small-editor", shim: false, completeOnEnter: true, cancelOnEsc: true, labelSelector: "span.x-editable", updateEl: true, init: function (a) {
    this.view = a;
    a.on("render", this.initEditor, this);
    this.on("complete", this.onSave, this);
    this.on("beforehide", function (b) {
        b.boundEl.dom.innerHTML = b.boundEl.dom.innerHTML.gsub("\n", "<br/>")
    })
}, initEditor: function () {
    Ext.get(document).on("mousedown", this.onMouseDownBefore.bind(this));
    this.view.getEl().on("mousedown", this.onMouseDown, this, {delegate: this.labelSelector});
    this.view.getEl().on("mouseover", this.onMouseOver, this, {delegate: this.labelSelector});
    this.view.getEl().on("mouseout", this.onMouseOut, this, {delegate: this.labelSelector})
}, onMouseDownBefore: function (b, a) {
    if (this.field && this.field.getEl() && b.target != this.field.getEl().dom) {
        this.completeEdit()
    }
}, onMouseOver: function (b, a) {
    Ext.get(a).addClass("x-over")
}, onMouseOut: function (b, a) {
    Ext.get(a).removeClass("x-over")
}, onMouseDown: function (g, f) {
    if (!g.ctrlKey && !g.shiftKey) {
        g.stopEvent();
        if (this.dataIndex) {
            var h = this.dataIndex.split(".");
            this.dataKey = h[0];
            this.dataAttr = h.length >= 2 ? h[1] : null
        }
        this.field.setWidth(Math.max(f.offsetWidth, 150));
        this.field.allowBlank = f.className.indexOf("required") < 0;
        var d;
        if (this.view.findItemFromChild instanceof Function) {
            var b = this.view.findItemFromChild(f);
            var a = this.view.store.getAt(this.view.indexOf(b));
            this.activeRecord = a;
            if (this.dataAttr) {
                d = a.data[this.dataKey][this.dataAttr]
            } else {
                d = a.data[this.dataKey]
            }
        } else {
            d = f.innerHTML.gsub("<br/>", "\n").gsub("<br>", "\n").gsub("<BR>", "\n").gsub("<BR/>", "\n")
        }
        if (d !== undefined) {
            if (f.className.include("x-default")) {
                d = ""
            }
            this.startEdit(f, (d || "").unescapeHTML())
        }
    } else {
        g.preventDefault()
    }
}, onSave: function (b, d, a) {
    if (d === a) {
        return
    }
    if (this.activeRecord && this.dataKey && !this.dataAttr) {
        this.activeRecord.set(this.dataKey, d);
        this.activeRecord.commit()
    } else {
        if (this.activeRecord && this.dataKey && this.dataAttr) {
            var e = Object.clone(this.activeRecord.get(this.dataIndex));
            e[this.dataAttr] = d;
            this.activeRecord.set(this.dataKey, e);
            this.activeRecord.commit()
        }
    }
}});
Ext.DataView.DragSelector = function (j) {
    j = j || {};
    var m, f, l, q, b;
    var g, n, p = new Ext.lib.Region(0, 0, 0, 0);
    var d = j.dragSafe === true;
    this.init = function (u) {
        m = u;
        m.on("render", t)
    };
    this.isDragging = function () {
        return !!l
    };
    function r() {
        g = [];
        m.all.each(function (u) {
            g[g.length] = u.getRegion()
        });
        n = m.el.getRegion()
    }

    function h() {
        return true
    }

    function k(w) {
        var v = m.el.isScrollable();
        var u = 18;
        return((!d || w.target == m.el.dom) && (!v || ((m.el.dom.offsetWidth + m.el.getX()) - w.getXY()[0]) > u))
    }

    function s(u) {
        window.clearTimeout(b);
        m.on("containerclick", h, m, {single: true});
        if (!l) {
            l = m.el.createChild({cls: "x-view-selector"})
        } else {
            l.setDisplayed("block")
        }
        r();
        if (!this.ctrlKey) {
            m.clearSelections()
        }
    }

    function e(C) {
        var D = q.startXY;
        var H = q.getXY();
        var F = Math.min(D[0], H[0]);
        var E = Math.min(D[1], H[1]);
        var G = Math.abs(D[0] - H[0]);
        var A = Math.abs(D[1] - H[1]);
        p.left = F;
        p.top = E;
        p.right = F + G;
        p.bottom = E + A;
        p.constrainTo(n);
        l.setRegion(p);
        for (var z = 0, B = g.length; z < B; z++) {
            var u = g[z], v = p.intersect(u);
            if (v && !u.selected) {
                u.selected = true;
                m.select(z, true)
            } else {
                if (!v && u.selected) {
                    u.selected = false;
                    m.deselect(z)
                }
            }
        }
    }

    function a(u) {
        if (l) {
            l.setDisplayed(false);
            window.clearTimeout(b);
            b = window.setTimeout(function () {
                l.remove();
                l = null
            }, 10)
        }
    }

    function t(u) {
        q = new Ext.dd.DragTracker({onBeforeStart: k, onMouseMove: function (v) {
            this.ctrlKey = v.ctrlKey;
            Ext.dd.DragTracker.prototype.onMouseMove.apply(this, arguments)
        }, onStart: s, onDrag: e, onEnd: a});
        q.initEl(u.el)
    }
};
Ext.grid.DragSelector = function (j) {
    j = j || {};
    var n, a, b, r, g, m;
    var k, h, e = new Ext.lib.Region(0, 0, 0, 0);
    var q = j.dragSafe === true;
    this.init = function (v) {
        a = v;
        a.on("render", f)
    };
    this.isDragging = function () {
        return !!r
    };
    function d() {
        k = [];
        $A(n.getRows() || []).each(function (v) {
            k[k.length] = Ext.get(v).getRegion()
        });
        h = n.el.getRegion()
    }

    function s() {
        return true
    }

    function u(x) {
        var w = n.scroller.isScrollable();
        var v = 18;
        return((!q || x.target == n.el.dom || n.el.contains(x.target)) && (!w || ((n.el.dom.offsetWidth + n.el.getX()) - x.getXY()[0]) > v))
    }

    function t(v) {
        window.clearTimeout(m);
        n.on("click", s, n, {single: true});
        if (!r) {
            r = n.el.createChild({cls: "x-view-selector"})
        } else {
            r.setDisplayed("block")
        }
        d();
        if (!this.ctrlKey) {
            a.getSelectionModel().clearSelections()
        }
    }

    function p(E) {
        var F = g.startXY;
        var J = g.getXY();
        var H = Math.min(F[0], J[0]);
        var G = Math.min(F[1], J[1]);
        var I = Math.abs(F[0] - J[0]);
        var C = Math.abs(F[1] - J[1]);
        e.left = H;
        e.top = G;
        e.right = H + I;
        e.bottom = G + C;
        e.constrainTo(h);
        r.setRegion(e);
        for (var B = 0, D = k.length; B < D; B++) {
            var v = k[B], z = e.intersect(v), A = a.getSelectionModel();
            if (z && !v.selected) {
                v.selected = true;
                A.selectRow(B, true)
            } else {
                if (!z && v.selected) {
                    delete v.selected;
                    A.deselectRow(B)
                }
            }
        }
    }

    function l(v) {
        if (r) {
            r.setDisplayed(false);
            window.clearTimeout(m);
            m = window.setTimeout(function () {
                r.remove();
                r = null
            }, 10)
        }
    }

    function f() {
        n = a.view;
        g = new Ext.dd.DragTracker({onBeforeStart: u, onMouseMove: function (v) {
            this.ctrlKey = v.ctrlKey;
            Ext.dd.DragTracker.prototype.onMouseMove.apply(this, arguments)
        }, onStart: t, onDrag: p, onEnd: l});
        g.initEl(n.el)
    }
};
Ext.data.Record.prototype.set = function (b, d, a) {
    if (this.data[b] === d) {
        return
    }
    this.dirty = true;
    if (!this.modified) {
        this.modified = {}
    }
    if (typeof this.modified[b] == "undefined") {
        this.modified[b] = this.data[b]
    }
    this.data[b] = d;
    if (!this.editing && this.store && a !== true) {
        this.store.afterEdit(this)
    }
};
Ext.data.Record.prototype.commit = function (a) {
    if (this.store && a !== true && this.store.beforeCommit) {
        this.store.beforeCommit(this)
    }
    this.dirty = false;
    delete this.modified;
    this.editing = false;
    if (this.store && a !== true) {
        this.store.afterCommit(this)
    }
};
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Extensions) {
    Signavio.Extensions = {}
}
Signavio.Extensions.AddedPanel = function (a) {
    a = a || {};
    this.store = a.store;
    if (a.onDelete instanceof Function) {
        this.plugins = [new Signavio.Extensions.HoverButton({selector: "div.x-panel-added-item", callback: this.doDelete.bind(this)})]
    }
    this.hideAddField = a.hideAddField === false || !(a.onAppend instanceof Function);
    Signavio.Extensions.AddedPanel.superclass.constructor.call(this, a);
    this.itemTpl.compile()
};
Ext.extend(Signavio.Extensions.AddedPanel, Ext.Panel, {itemTpl: new Ext.XTemplate('<div class="x-panel-added-list">', '<tpl for=".">', '<div class="x-panel-added-item" rel="{id}"><img src="{icon}"/><span>{name}</span></div>', "</tpl>", "</div>"), icon: Signavio.Config.EXPLORER_PATH + "/src/img/famfamfam/user.png", cls: "x-panel-added", width: 200, isFormField: true, originalValue: null, border: false, onRender: function () {
    Signavio.Extensions.AddedPanel.superclass.onRender.apply(this, arguments);
    var a = new Ext.XTemplate('<tpl for="."><tpl for="rep"><div class="search-item">', '<span class="name">{name}</span>', '<span class="mail">{mail}</span>', '<span class="principal">{principal}</span>', "</div></tpl></tpl>");
    this.textField = new Ext.form.ComboBox(Ext.apply({store: this.store, hidden: this.hideAddField, width: this.width, minListWidth: 200, listClass: "x-panel-added-cb-list", displayField: "rep.name||rep.mail||rep.principal", value: "", tpl: a, itemSelector: "div.search-item", blankText: "Name", selectOnFocus: true, triggerClass: "x-panel-added-trigger", renderTo: this.body, allowBlank: true, mode: "local", tabIndex: 1000, forceSelection: true, minChars: 1, disabled: this.disabled, listeners: {specialkey: function (b, d) {
        if (d.getKey() === Ext.EventObject.ENTER) {
            this.onTrigger(this.textField.getValue(), "principal");
            this.textField.focus(false, 100)
        }
    }.bind(this)}, onTriggerClick: function (b) {
        if (!b || b != Ext.EventObject || b.type !== "click" || this.onTrigger(this.textField.getValue(), "principal")) {
            Ext.form.ComboBox.prototype.onTriggerClick.apply(this.textField, arguments)
        }
    }.bind(this), onTypeAhead: function () {
        if (this.store.getCount() > 0) {
            var d = this.store.getAt(0);
            var e = d.get("rep").name;
            var b = e.length;
            var f = this.getRawValue().length;
            if (f != b) {
                this.setRawValue(e);
                this.selectText(f, e.length)
            }
        }
    }, onSelect: function (b, d) {
        if (this.fireEvent("beforeselect", this, b, d) !== false) {
            this.setValue(b.get("rep").principal);
            this.collapse();
            this.fireEvent("select", this, b, d)
        }
    }}, this.cbConfig || {}));
    this.list = document.createElement("div");
    this.body.appendChild(this.list);
    this.list = Ext.Element(this.list)
}, doDelete: function (d, f) {
    var b = f.getAttribute("rel");
    if (!b || !this.currentRecords) {
        return
    }
    var a = this.currentRecords.find(function (e) {
        return e.get("href") == b
    });
    if (!a) {
        return
    }
    this.onDelete(a, b)
}, onTrigger: function (d, a) {
    if (!d) {
        return true
    }
    var b = this.store.data.find(function (e) {
        return e.get("rep")[a] == d
    });
    if (!b) {
        return true
    }
    this.onAppend(b, d);
    this.textField.setValue("");
    return false
}, onAppend: function () {
}, onDelete: function () {
}, disable: function () {
    if (this.list) {
        this.list.addClass("x-disabled")
    }
    if (this.textField) {
        this.textField.disable()
    }
    this.disabled = true
}, enable: function () {
    if (this.list) {
        this.list.removeClass("x-disabled")
    }
    if (this.textField) {
        this.textField.enable()
    }
    this.disabled = false
}, setValue: function (b, d, f) {
    if (!this.body) {
        return
    }
    if (!(b instanceof Array)) {
        b = [b].compact()
    }
    this.currentRecords = f;
    if (b.length <= 0 && typeof this.blankText == "string") {
        b.push(this.blankText);
        this.list.addClass("x-no-group")
    } else {
        this.list.removeClass("x-no-group")
    }
    var e = b.map(function (g, a) {
        return{icon: this.icon, name: (this.parseText ? this.parseText(g, f[a]) : false) || g.name || g, id: f.length > a ? (f[a].get ? f[a].get("href") : f[a].href) : null}
    }.bind(this)).sort(function (h, g) {
        return Number(h.name.toLowerCase() > g.name.toLowerCase())
    });
    this.itemTpl.overwrite(this.list, e || [])
}});
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Extensions) {
    Signavio.Extensions = {}
}
new function () {
    function a(m, b, e, d) {
        m = m || {};
        var g = m.sortingFn || undefined;
        var f = typeof m == "string" ? m : m.stateValue;
        var j = m.treeFilter || function () {
            return true
        };
        var k = m.recordRel || ("user");
        var h = new Ext.tree.TreeNode({text: "", leaf: false, childLeaf: m.treeChildLeaf || false, cls: "folder", expanded: true, dataField: m.dataField || "rep.name", filterFn: j, childCls: m.treeChildCls || "user", recordRel: m.recordRel || "info", identifier: m.treeRootIdentifier, parseText: m.parseText instanceof Function ? m.parseText : undefined});
        var l = new Signavio.Extensions.TreePanel({sortingFn: g, cls: "administration_plugin_tree " + (m.treeCls || ""), region: "west", title: m.treeTitle || "", width: 200, split: m.split === false ? false : true, margins: m.split === false ? "3 6 3 3" : "3 0 3 3", root: h, tbar: e});
        l.getSelectionModel().on("selectionchange", function (p, q) {
            var n;
            if (b && b instanceof Signavio.Extensions.FormStorePanel) {
                if (q && q.attributes.identifier) {
                    n = Signavio.Core.StoreManager.getRelatedStore(q.attributes.identifier);
                    b.setStore(n)
                } else {
                    if (!q) {
                        b.setStore()
                    }
                }
            }
            if (d instanceof Function) {
                d(q, n)
            }
        });
        return l
    }

    Signavio.Extensions.AdminPanel = function (b) {
        b = b || {};
        var d = b.tbar;
        delete b.tbar;
        Ext.apply(this, b);
        this.form = this.generateFormPanel(b.items);
        this.onStoreChange = (b.onStoreChange || function () {
        }).bind(this);
        this.onSelectionChange = (b.onSelectionChange || function () {
        }).bind(this);
        this.tree = a(b, this.form, d, this.onSelectionChange);
        this.form.on("storechanged", function (e) {
            this.onStoreChange(this.tree.getSelectionModel().getSelectedNode(), e)
        }.bind(this));
        b.items = [this.tree, this.form];
        Signavio.Extensions.AdminPanel.superclass.constructor.call(this, b)
    };
    Ext.extend(Signavio.Extensions.AdminPanel, Ext.Panel, {layout: "border", margins: "3 3 3 3", generateFormPanel: function (b) {
        return new Signavio.Extensions.FormStorePanel({region: "center", autoScroll: true, cls: "configuration_window_form", margins: "3 3 3 0", items: b, bodyStyle: "overflow-x:hidden;", hideButtons: this.hideButtons || false})
    }})
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Extensions) {
    Signavio.Extensions = {}
}
Signavio.Extensions.DrawerButtons = function (a) {
    a = a || {};
    var e = [];
    this.init = function (j) {
        if (j.layout instanceof Ext.layout.CardLayout || j.layout == "card") {
            this.view = j;
            j.on("render", f, this);
            j.on("add", b, this);
            j.on("collapse", d, this);
            j.on("expand", g, this)
        }
    };
    var f = function () {
        var j = ["div", {"class": "drawer_plugin"}];
        this.root = Signavio.Core.graft("http://www.w3.org/1999/xhtml", this.view.getEl().dom.parentNode, j);
        if (!this.view.items || this.view.items.length <= 0) {
            return
        }
        this.view.items.each(function (l, k) {
            b(this, l, k)
        }.bind(this));
        h(0)
    };
    var b = function (m, l, j) {
        var k = Signavio.Core.graft("http://www.w3.org/1999/xhtml", null, ["a", {"class": "x-btn"}]);
        k.innerHTML = l.title + " &raquo;";
        k.addEventListener("click", h.bind(this, j), true);
        this.root.appendChild(k);
        e.push(k)
    };
    var g = function () {
        this.view.el.appendChild(this.root);
        if (!this.view.layout.activeItem) {
            h.call(this, 0)
        }
    };
    var d = function () {
        $(this.view.id + "-xcollapsed").appendChild(this.root)
    };
    var h = function (j) {
        if (this.view.layout.activeItem) {
            var k = this.view.items.items.indexOf(this.view.layout.activeItem);
            if (k === j) {
                this.view.collapse(true);
                return
            }
            e[k].className = e[k].className.replace(" activated", "")
        } else {
            if (this.view.collapsed) {
                this.view.expand(true)
            }
        }
        e[j].className += " activated";
        this.view.layout.setActiveItem(j);
        return false
    }
};
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Extensions) {
    Signavio.Extensions = {}
}
Signavio.Extensions.HoverButton = function (b) {
    b = b || {};
    var d = b.tpl || new Ext.XTemplate('<a class="x-hoverbutton">' + Signavio.I18N.Repository.HoverButton.deleteString + "</a>");
    d.compile();
    var g;
    var j;
    var h = b.callback || function () {
    };
    this.init = function (k) {
        this.view = k;
        k.on("render", e, this)
    };
    var e = function () {
        this.view.getEl().on("mouseover", f, this, {delegate: b.selector});
        this.view.getEl().on("mouseout", a, this, {delegate: b.selector})
    };
    var f = function (k, l) {
        window.clearTimeout(j);
        if (g && g.parent(null, true) == l) {
            return
        } else {
            if (g) {
                g.remove()
            }
        }
        g = d.insertFirst(l, {}, true);
        g.on("click", function (n, m) {
            h.apply(h, [n, m.parentNode])
        })
    };
    var a = function (k, l) {
        j = window.setTimeout(function () {
            if (g) {
                g.remove()
            }
        }, 50)
    }
};
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Extensions) {
    Signavio.Extensions = {}
}
Signavio.Extensions.FormStorePanel = function (a) {
    a = a || {};
    Signavio.Extensions.FormStorePanel.superclass.constructor.call(this, a);
    this.setStore(a.store)
};
Ext.extend(Signavio.Extensions.FormStorePanel, Ext.form.FormPanel, {defaults: {width: 200}, getDirtyEl: function () {
    return Signavio.Core.graft("http://www.w3.org/1999/xhtml", null, ["div", {"class": "x-grid3-dirty-cell x-dirty-cell-overlay"}])
}, getWaitingEl: function () {
    return Signavio.Core.graft("http://www.w3.org/1999/xhtml", null, ["div", {"class": "x-waiting"}])
}, setDirty: function (a) {
    if (!a.dirtyEl) {
        a.dirtyEl = this.getDirtyEl().cloneNode(true)
    }
    if (a.body || a.el) {
        (a.body || a.el.parent()).appendChild(a.dirtyEl)
    }
}, removeDirty: function (a) {
    if (a.dirtyEl && a.dirtyEl.parentNode) {
        a.dirtyEl.parentNode.removeChild(a.dirtyEl)
    }
}, setWaiting: function (a) {
    if (!this.el) {
        return
    }
    if (!this.waitingEl) {
        this.waitingEl = this.getWaitingEl().cloneNode(true)
    }
    window.clearTimeout(this.waitingTimeout);
    if (a === true) {
        this.waitingTimeout = window.setTimeout(this.el.appendChild.bind(this.el, this.waitingEl), 10)
    } else {
        if (a === false && this.waitingEl.parentNode) {
            this.waitingEl.parentNode.removeChild(this.waitingEl)
        }
    }
}, initFields: function () {
    var e = function (l, k, j) {
        this[j === String(k.originalValue) ? "removeDirty" : "setDirty"](l);
        if (k.dataField && this.store) {
            var h = this.store.getRecords(l.recordRel)[0] || this.store.getAt(0);
            var m = Object.clone(h.get("rep"));
            m[k.dataField.split(".").last()] = l.parseData ? l.parseData(j) : j;
            h.set("rep", m)
        }
    };
    this.items.each(function (h) {
        if (h instanceof Ext.form.TextField || h instanceof Ext.form.Checkbox) {
            h.on("change", e.bind(this, h))
        } else {
            if (h instanceof Ext.tree.TreePanel) {
                h.on("checkchange", function (n, m) {
                    var q = h.getValue();
                    var l = h.originalValue;
                    var j = !l || (l.length === q.length && l.all(function (r) {
                        return q.include(r)
                    }));
                    this[j ? "removeDirty" : "setDirty"](h);
                    if (h.dataField && this.store) {
                        var k = this.store.getRecords(h.recordRel)[0] || this.store.getAt(0);
                        var p = Object.clone(k.get("rep"));
                        p[h.dataField.split(".").last()] = q;
                        k.set("rep", p)
                    }
                }.bind(this))
            } else {
                if (h instanceof Ext.TabPanel) {
                    h.on("tabchange", function (k, l) {
                        if (l.value && this.store && h.dataField) {
                            var j = this.store.getRecords(h.recordRel)[0] || this.store.getAt(0);
                            if (!j) {
                                return
                            }
                            var m = Object.clone(j.get("rep"));
                            m[h.dataField.split(".").last()] = l.value;
                            j.set("rep", m)
                        }
                    }.bind(this));
                    h.items.each(function (j) {
                        if (j instanceof Ext.Panel) {
                            j.items.each(function (k) {
                                if (k instanceof Ext.form.TextField || k instanceof Ext.form.Checkbox) {
                                    k.on("change", e.bind(this, k))
                                }
                            }.bind(this))
                        }
                    }.bind(this))
                }
            }
        }
    }.bind(this));
    Signavio.Extensions.FormStorePanel.superclass.initFields.call(this);
    this.ok = Ext.get(document.createElement("img"));
    this.failed = Ext.get(document.createElement("img"));
    this.ok.dom.src = Signavio.Config.LIBS_PATH + "/ext-2.0.2/resources/images/default/tree/s.gif";
    this.failed.dom.src = Signavio.Config.LIBS_PATH + "/ext-2.0.2/resources/images/default/tree/s.gif";
    this.ok.addClass("x-ok");
    this.failed.addClass("x-failed");
    var f = 1000;
    var b = false;
    var d = function (h) {
        this.failed.show(true);
        window.setTimeout(function () {
            this.failed.hide({useDisplay: true});
            b = false
        }.bind(this), f);
        h.un("submitfailed", g, this);
        h.un("update", g, this);
        b = true
    };
    var g = function (j, h, k) {
        if (k !== Ext.data.Record.COMMIT || b === true) {
            return
        }
        this.ok.show(true);
        window.setTimeout(this.ok.hide.bind(this.ok, {useDisplay: true}), f);
        j.un("update", g, this);
        j.un("submitfailed", g, this)
    };
    this.commitBtn = new Ext.Button({text: Signavio.I18N.Repository.FormStorePanel.commit, disabled: true, hidden: this.hideButtons, handler: function () {
        if (this.store) {
            this.store.commitChanges();
            this.store.on("update", g, this);
            this.store.on("submitfailed", d, this)
        }
    }.bind(this)});
    this.rejectBtn = new Ext.Button({text: Signavio.I18N.Repository.FormStorePanel.reject, disabled: true, hidden: this.hideButtons, handler: function () {
        this.store ? this.store.rejectChanges() : null
    }.bind(this)});
    var a = new Ext.Panel({border: false, disable: this.disable.bind(a), enable: this.enable.bind(a), autoWidth: true, style: this.labelWidth !== undefined ? "padding-left:" + (this.labelWidth + 5) + "px" : "", cls: "x-admin-btn", items: ([this.commitBtn, this.rejectBtn])});
    a.on("render", function () {
        a.body.appendChild(this.ok.dom);
        a.body.appendChild(this.failed.dom)
    }.bind(this));
    this.add(a);
    this.items.each(function (h) {
        if (h.initialConfig.width && h.initialConfig.width != h.width) {
            h.setWidth(h.initialConfig.width)
        }
    })
}, showButtons: function (a) {
    this.commitBtn.setVisible(a !== false);
    this.rejectBtn.setVisible(a !== false)
}, onRender: function () {
    Signavio.Extensions.FormStorePanel.superclass.onRender.apply(this, arguments);
    if (this.commitBtn && this.store && !this.disabled) {
        this.commitBtn.setDisabled(false);
        this.rejectBtn.setDisabled(false)
    }
    if (this.store && this.store.data.items.size() > 0) {
        window.setTimeout(this.doStoreChange.bind(this, this.store), 10)
    }
}, store: null, setStore: function (a) {
    this.setWaiting(true);
    if (this.store) {
        this.store.un("load", this.doStoreChange, this);
        this.store.un("update", this.doStoreChange, this);
        this.store.un("remove", this.doStoreChange, this);
        this.store.un("add", this.doStoreChange, this)
    }
    this.store = a;
    if (this.store) {
        this.store.on("load", this.doStoreChange, this);
        this.store.on("update", this.doStoreChange, this);
        this.store.on("remove", this.doStoreChange, this);
        this.store.on("add", this.doStoreChange, this)
    }
    if (this.commitBtn) {
        this.commitBtn.setDisabled(!a);
        this.rejectBtn.setDisabled(!a)
    }
    if (!a) {
        this.doStoreChange(true)
    }
    if (this.store && this.store.data.items.size() > 0) {
        window.setTimeout(this.doStoreChange.bind(this, this.store), 10)
    }
}, disable: function () {
    if (this.items) {
        this.items.each(function (a) {
            a.disable()
        });
        this.disabled = true
    }
}, enable: function () {
    if (this.items) {
        this.items.each(function (a) {
            if (a.initialConfig.disabled) {
                return
            }
            a.enable()
        });
        this.disabled = false
    }
}, doStoreChange: function (e, d, g) {
    if (!e || (typeof g == "string" && g !== Ext.data.Record.COMMIT)) {
        return
    }
    var f = e instanceof Signavio.Core.DataStore ? e : undefined;
    this.items.each(function (j) {
        if (typeof j.dataField === "string") {
            var l, b, k;
            if (f) {
                k = f.getRecords(j.recordRel) || f.data.items;
                l = (k || []).map(function (m) {
                    return f.parseValue(m, j.dataField)
                }).compact().reduce() || "";
                if (k) {
                    b = k.findAll(function (m) {
                        return !(f.addedRecords || []).include(m)
                    }).concat(f.removedRecords);
                    b = b.map(function (m) {
                        if (m) {
                            return f.parseValue(m.modified || m, j.dataField)
                        } else {
                            return null
                        }
                    }).compact().reduce()
                }
            }
            if (typeof l == "string") {
                l = Signavio.Utils.unescapeHTML(l)
            }
            if (typeof b == "string") {
                b = Signavio.Utils.unescapeHTML(b)
            }
            j.originalValue = b || l;
            if (j.setValue) {
                j.setValue(l || j.defaultValue || j.initialConfig.value, f, k, j)
            }
            var h = this.disabled || j.initialConfig.disabled || (j.value === j.defaultValue && !!j.value) || (j.initialConfig.isDisabled instanceof Function && j.initialConfig.isDisabled(l, (k || []).reduce()));
            j[(j.value == j.defaultValue && !!j.value) ? "addClass" : "removeClass"]("x-default");
            j[h ? "disable" : "enable"]();
            var a = !b && !l;
            if (typeof l == "string") {
                a = a || (!b || l === String(b) || (b instanceof Array && b.include(l)))
            } else {
                if (l instanceof Array) {
                    a = a || (b && (b.length === l.length && b.all(function (m) {
                        var n = $H(m).toJSON();
                        return !!l.find(function (p) {
                            return $H(p).toJSON() == n
                        })
                    })))
                } else {
                    a = a || (b && ($H(b).toJSON() === $H(l).toJSON()))
                }
            }
            a = j.hideAddField || a;
            this[a ? "removeDirty" : "setDirty"](j)
        }
    }.bind(this));
    if (this.commitBtn) {
        this.commitBtn.enable();
        this.rejectBtn.enable()
    }
    if (this.disabled && f) {
        this.enable()
    } else {
        if (!this.disabled && !f) {
            this.disable()
        }
    }
    this.setWaiting(false);
    this.fireEvent("storechanged", f)
}});
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Extensions) {
    Signavio.Extensions = {}
}
Signavio.Extensions.TreeLoader = function () {
    this.cachedStores = [];
    this.cachedCallbacks = {};
    this.getRequestUrl = undefined;
    Signavio.Extensions.TreeLoader.superclass.constructor.apply(this, arguments)
};
Ext.extend(Signavio.Extensions.TreeLoader, Ext.tree.TreeLoader, {findRecord: function (b) {
    var a;
    (this.cachedStores || []).any(function (d) {
        a = d.data.items.find(function (e) {
            return e.get("href") === b
        });
        return !!a
    });
    return a
}, dataUrl: true, requestData: function (d, e) {
    if (d.attributes.identifier) {
        d.collapse();
        var b = this.getRequestUrl ? this.getRequestUrl(d.attributes.identifier) : d.attributes.identifier;
        var a = Signavio.Core.StoreManager.getRelatedStore(b, this.storekey);
        this.initStoreEvents(a, d, e)
    } else {
        this.fireEvent("update", this, d);
        if (typeof e == "function") {
            e(this, d)
        }
    }
}, setGetRequestUrlCallback: function (a) {
    this.getRequestUrl = a
}, initStoreEvents: function (a, b, e) {
    var d = b.id + (b.attributes.identifier || a.getIdentifier());
    if (this.cachedCallbacks[d]) {
        a.un("load", this.cachedCallbacks[d].update);
        a.un("update", this.cachedCallbacks[d].update);
        a.un("add", this.cachedCallbacks[d].add);
        a.un("remove", this.cachedCallbacks[d].remove)
    }
    this.cachedCallbacks[d] = {update: this.doResponse.bind(this, a, b, e), add: this.onAdd.bind(this, b), remove: this.onRemove.bind(this, b)};
    a.on("load", this.cachedCallbacks[d].update);
    a.on("update", this.cachedCallbacks[d].update);
    a.on("add", this.cachedCallbacks[d].add);
    a.on("remove", this.cachedCallbacks[d].remove);
    if (!this.cachedStores.include(a)) {
        this.cachedStores.push(a)
    }
    if (a.lastOptions && b.rendered) {
        this.cachedCallbacks[d].update()
    }
}, releaseAllEvents: function () {
    (this.cachedStores || []).each(function (a) {
        var d = a.lastOptions.id;
        var b = $H(this.cachedCallbacks).keys().find(function (e) {
            return e.endsWith(d)
        });
        if (this.cachedCallbacks && this.cachedCallbacks[b]) {
            a.un("load", this.cachedCallbacks[b].update);
            a.un("update", this.cachedCallbacks[b].update);
            a.un("add", this.cachedCallbacks[b].add);
            a.un("remove", this.cachedCallbacks[b].remove)
        }
    })
}, onAdd: function (e, d, b) {
    var a = [e.attributes.recordRel].flatten();
    if (b.any(function (g) {
        return !a.include(g.get("rel"))
    })) {
        return
    }
    e.attributes.leaf = false;
    e.expand(false, true);
    var f = [];
    this.generateNodeData(d, b, e).each(function (j) {
        var h = b.find(function (l) {
            return l.get("href") === j.identifier
        });
        var g = ([]).concat(d.data.items).concat([h]).findAll(function (l) {
            return l.get("rel") === "dir"
        }).sort(d.lastOptions.sort || Signavio.Helper.BusinessObjectSortingFunction);
        var k = this.createNode(j);
        f.push(k);
        if (h && g.indexOf(h) + 1 < g.length) {
            e.insertBefore(k, e.item(g.indexOf(h)))
        } else {
            e.appendChild(k)
        }
    }.bind(this));
    f.each(function (g) {
        Ext.get(g.ui.elNode).slideIn("t", {duration: 0.2})
    });
    if (this.showTooltip) {
        f.each(function (g) {
            (g.getUI().wrap.dom || g.getUI().wrap).setAttribute("title", g.text)
        })
    }
}, onRemove: function (f, d, b) {
    var a = [f.attributes.recordRel].flatten();
    if (!a.include(b.get("rel"))) {
        return
    }
    var e = f.findChild("identifier", b.get("href"));
    if (e && e.getOwnerTree()) {
        e.remove()
    }
}, generateNodeData: function (d, b, e) {
    if (this.sortingFn) {
        b = b.sort(this.panel.sortingFn || this.sortingFn)
    } else {
        if (!(d.lastOptions.sort instanceof Function)) {
            b = b.sort(Signavio.Helper.BusinessObjectSortingFunction)
        }
    }
    e.attributes.store = d;
    var a = b.map(function (h) {
        var g = e.attributes.filterFn;
        if (g instanceof Function && !g(h)) {
            return
        }
        var f = e.attributes;
        var k = d.parseValue(h, e.attributes.dataField);
        var j = {text: Signavio.I18N.Repository.Folder[h.get("rep").type] || (f.parseText ? f.parseText(k, h) : false) || k || f.value, cls: f.childCls || (f.hasChildCls ? f.hasChildCls(h) : ""), leaf: f.childLeaf || (f.isChildLeaf ? f.isChildLeaf(h) : false), childLeaf: f.childLeaf, isChildLeaf: f.isChildLeaf, filterFn: f.filterFn, checked: f.checked ? e.ui.isChecked() : (f.checked === false ? e.ui.isChecked() : undefined), hasChildCls: f.hasChildCls, parseText: f.parseText instanceof Function ? f.parseText : undefined, childCls: f.childCls, dataField: f.dataField, recordRel: f.recordRel, singleClickExpand: f.singleClickExpand || false, getIdentifier: f.getIdentifier instanceof Function ? f.getIdentifier : undefined, getToolTip: f.getToolTip instanceof Function ? f.getToolTip : undefined, identifier: f.getIdentifier instanceof Function ? f.getIdentifier(h) : h.get("href")};
        if (this.showTooltip) {
            j.qtip = (f.getToolTip instanceof Function ? f.getToolTip(h) : "") || j.text
        }
        if (this.panel && this.panel.disabledIds && this.panel.disabledIds.include(j.identifier)) {
            return null
        }
        return j
    }.bind(this));
    return a.compact()
}, doResponse: function (b, e, f) {
    if ((typeof c == "string" && c !== Ext.data.Record.COMMIT) || !e.getOwnerTree()) {
        return
    }
    var a = this.generateNodeData(b, b.getRecords(e.attributes.recordRel), e);
    if (e.childNodes.length > 0 && e.childNodes.length === a.length && e.childNodes.all(function (g) {
        return a.any(function (h) {
            return g.attributes.identifier === h.identifier && g.attributes.text === h.text
        })
    })) {
        return
    }
    e.collapse();
    var d = e.childNodes.length - 1;
    for (; d >= 0; d--) {
        e.childNodes[d].remove()
    }
    e.beginUpdate();
    a.each(function (g) {
        var h = this.createNode(g);
        e.appendChild(h);
        if (this.showTooltip) {
        }
    }.bind(this));
    e.endUpdate();
    window.setTimeout(function () {
        e.expand()
    }, 100);
    this.fireEvent("update", this, a);
    this.fireEvent("load", this, e);
    if (typeof f == "function") {
        f(this, e)
    }
}});
Signavio.Extensions.TreePanel = function (b) {
    b = b || {};
    b.loader = new Signavio.Extensions.TreeLoader({panel: this, sortingFn: b.sortingFn});
    Signavio.Extensions.TreePanel.superclass.constructor.call(this, b);
    this.loader.on("update", this.onUpdate.bind(this));
    if (b.root instanceof Ext.tree.TreeNode) {
        this.on("render", function () {
            this.getLoader().requestData(this.root, this.onRootLoaded.bind(this))
        }.bind(this))
    } else {
        if (b.data) {
            var a = new Ext.tree.TreeNode({text: "", draggable: false, expanded: true});
            this.setRootNode(a);
            this.appendChildNodes(a, b.data)
        }
    }
};
Ext.extend(Signavio.Extensions.TreePanel, Ext.tree.TreePanel, {animate: true, enableDD: false, rootVisible: false, useArrows: true, autoScroll: true, onRootLoaded: function () {
}, onUpdate: function () {
}, appendChildNodes: function (a, b) {
    return b.map(function (e) {
        if (e.useTreeNode === true) {
            return a.appendChild(new Ext.tree.TreeNode(e))
        } else {
            return a.appendChild(this.getLoader().createNode(e))
        }
    }.bind(this))
}, removeChildNodes: function (b) {
    var a = b.childNodes.length - 1;
    for (; a >= 0; a--) {
        b.childNodes[a].remove()
    }
}, getSelectedRecord: function () {
    var b = this.getSelectionModel().getSelectedNode();
    if (!b) {
        return undefined
    }
    var a = this.loader.findRecord(b.attributes.identifier);
    return a
}, onRender: function () {
    Signavio.Extensions.TreePanel.superclass.onRender.apply(this, arguments);
    if (this.unselectable === true) {
        this.el.on("click", function (a, d) {
            if (d === this.body.dom) {
                this.selectPath("")
            }
        }.bind(this))
    }
}, onDestroy: function () {
    if (this.rendered) {
        this.getLoader().releaseAllEvents()
    }
}});
Signavio.Extensions.TreeCheckPanel = function (b) {
    b = b || {};
    this.data = b.data;
    this.dataLabel = b.dataLabel;
    delete b.data;
    Signavio.Extensions.TreeCheckPanel.superclass.constructor.call(this, b);
    if (b.dataStore instanceof Ext.data.Store) {
        this.data = [];
        b.dataStore.on("load", function (d) {
            this.data = d.data.items.map(function (e) {
                return e.data
            });
            this.loadData()
        }.bind(this))
    }
    if (this.data instanceof Array) {
        var a = new Ext.tree.TreeNode({text: "", draggable: false, expanded: true});
        this.setRootNode(a)
    }
};
Ext.extend(Signavio.Extensions.TreeCheckPanel, Signavio.Extensions.TreePanel, {animate: true, autoScroll: true, enableDD: false, rootVisible: false, useArrows: true, cls: "x-tree-check-panel", loader: new Ext.tree.TreeLoader(), value: null, height: 100, width: 129, isFormField: true, checkRecursive: false, originalValue: null, defaultItem: {leaf: true, checked: false, useTreeNode: true, iconCls: "x-hidden"}, afterRender: function () {
    Signavio.Extensions.TreeCheckPanel.superclass.afterRender.call(this);
    this.loadData()
}, loadData: function () {
    var a = function (e, b) {
        e = e.map(function (d) {
            return Ext.apply(Object.clone(this.defaultItem), (typeof d == "string" ? {text: d, identifier: d} : (this.dataLabel ? {text: d[this.dataLabel], identifier: d[this.dataLabel]} : d)))
        }.bind(this));
        var f = this.appendChildNodes(b, e);
        f.each(function (d) {
            if (d.attributes.children instanceof Array) {
                a(d.attributes.children, d)
            }
            d.render()
        })
    }.bind(this);
    if (this.data && this.root) {
        a(this.data, this.root)
    }
}, setValue: function (b) {
    this.value = b;
    if (b instanceof Array) {
        this.root.cascade(function (d) {
            this.toggleCheck(d, false);
            d.enable()
        }.bind(this));
        var a = function (f, d, h, e) {
            var g = f.findChild(d, h);
            if (!g && e) {
                f.childNodes.any(function (j) {
                    g = a(j, d, h, e);
                    return !!g
                })
            }
            return g
        };
        b.each(function (d) {
            this.toggleCheck(a(this.root, "identifier", this.dataLabel ? d[this.dataLabel] : d, this.checkRecursive), true)
        }.bind(this))
    }
}, getValue: function () {
    var a = [];
    this.root.cascade(function (b) {
        if (b.ui.isChecked()) {
            if (this.dataLabel) {
                var d = this.data.find(function (e) {
                    return e[this.dataLabel] == (b.identifier || b.text)
                }.bind(this));
                if (d) {
                    a.push(d)
                }
            } else {
                a.push(b.identifier || b.text)
            }
        }
    }.bind(this));
    return a
}, toggleCheck: function (b, d, a) {
    if (!b) {
        return
    }
    b.beginUpdate();
    b.ui.toggleCheck(d);
    b.endUpdate();
    if (a !== true) {
    }
}, onDestroy: function () {
    Signavio.Extensions.TreeCheckPanel.superclass.onDestroy.call(this)
}});
Signavio.Extensions.TreeTableView = function (a) {
    a = a || {};
    if (a.store) {
        this.store = a.store;
        this.store.on("load", this.onLoad.bind(this));
        this.store.on("update", this.onLoad.bind(this))
    }
    a.root = new Ext.tree.TreeNode({text: "RootFolder", leaf: false, cls: "folder"});
    Signavio.Extensions.TreeTableView.superclass.constructor.call(this, a);
    if (a.forceUpdate === true) {
        this.onLoad(this.store, this.store.data.items, this.store.lastOptions)
    }
    if (a.listeners && a.listeners.selectionchange) {
        this.getSelectionModel().on("selectionchange", a.listeners.selectionchange)
    }
};
Ext.extend(Signavio.Extensions.TreeTableView, Signavio.Extensions.TreePanel, {cls: "x-tableview-plugin", autoWidth: true, animate: true, border: false, onLoad: function (a, b, d) {
    this.root.collapse();
    this.dirty = true;
    this.root.attributes = {text: "RootFolder", leaf: false, cls: "folder", expanded: true, dataField: "rep.name", filterFn: function (e) {
        return true
    }, isChildLeaf: function (e) {
        return e.get("rel") === Signavio.Const.REL.MODEL
    }, hasChildCls: function (e) {
        return e.get("rel") === Signavio.Const.REL.MODEL ? "model" : "folder"
    }, recordRel: this.recordRel || [Signavio.Const.REL.DIRECTORY, Signavio.Const.REL.MODEL]};
    if (!this.viewPlugin || this.viewPlugin.isViewSelected()) {
        this.loader.doResponse(this.store, this.root, null, b, d);
        this.loader.initStoreEvents(this.store, this.root);
        this.dirty = false
    }
}});
Signavio.Extensions.ModelDirectoryCheckTreePanel = function (b, a) {
    this.initialSelection = a;
    b = b || {};
    if (!b.sortingFn) {
        b.sortingFn = Signavio.Helper.BusinessObjectSortingFunction
    }
    Signavio.Extensions.ModelDirectoryCheckTreePanel.superclass.constructor.call(this, b);
    this.addListener("checkchange", function (f, e) {
        var g = function (l, k) {
            var j = l.childNodes;
            for (var h = 0; h < j.length; h++) {
                j[h].ui.toggleCheck(k);
                j[h].attributes.checked = k;
                g(j[h], k)
            }
        };
        g(f, e);
        var d = function (j, k) {
            if (!j) {
                return
            }
            if (!k) {
                j.ui.toggleCheck(k);
                j.attributes.checked = k
            } else {
                var h = j.childNodes.all(function (l) {
                    return l.ui.isChecked()
                });
                if (h) {
                    j.ui.toggleCheck(true);
                    j.attributes.checked = true
                }
            }
            d(j.parentNode, k)
        };
        d(f.parentNode, e)
    })
};
Ext.extend(Signavio.Extensions.ModelDirectoryCheckTreePanel, Signavio.Extensions.TreePanel, {style: "padding:0px;padding-top:10px;", bodyStyle: "padding:2px 0px !important;", anchor: "100% -15", cls: "x-tableview-plugin x-div-textbackground", forceUpdate: true, recordRel: [Signavio.Const.REL.DIRECTORY, Signavio.Const.REL.MODEL], onRootLoaded: function () {
    window.setTimeout(function () {
        this.expandToAndCheckPreSelection(this.initialSelection || [])
    }.bind(this), 100)
}, onDestroy: function () {
    Signavio.Extensions.ModelDirectoryCheckTreePanel.superclass.onDestroy.call(this);
    this.getLoader().releaseAllEvents()
}, expandToAndCheckPreSelection: function (a) {
    if (!(a instanceof Array) && a.length == 0) {
        return
    }
    var d = [];
    var b = a.map(function (f) {
        return f instanceof Ext.data.Record ? f.get("href") : undefined
    }).compact();
    a.each(function (f) {
        var g = f instanceof Ext.data.Store ? f : Signavio.Core.StoreManager.getStore(f.get("href"));
        var h = ((g ? g.getRecords("parents") : []) || [])[0];
        if (h) {
            [].concat(g === f ? {href: g.getIdentifier()} : [], h.get("rep")).reverse().each(function (k, j) {
                if (!d[j]) {
                    d[j] = []
                }
                if (!d[j].include(k.href)) {
                    d[j].push(k.href)
                }
            })
        }
    });
    var e = function (f, g) {
        f.childNodes.each(function (h) {
            if ((d[g] || []).include(h.attributes.identifier)) {
                h.expand(false, false, function (j) {
                    e(h, g + 1)
                });
                if (g === d.length - 1 && h.attributes.checked === undefined) {
                    this.getSelectionModel().select(h)
                }
            }
            if (b.include(h.attributes.identifier)) {
                if (h.attributes.checked !== undefined) {
                    h.ui.toggleCheck(true);
                    h.attributes.checked = true
                } else {
                    this.getSelectionModel().select(h)
                }
            }
        }.bind(this))
    }.bind(this);
    e(this.root, 0)
}, findChildNode: function (a, f) {
    var d = a.childNodes;
    for (var b = 0; b < d.length; b++) {
        var e = d[b];
        if (e.attributes.identifier === f) {
            return e
        }
    }
}});
Signavio.Extensions.ProcessMapCheckTreePanel = function (a) {
    a = a || {};
    Signavio.Extensions.ModelDirectoryCheckTreePanel.superclass.constructor.call(this, a);
    a.loader.setGetRequestUrlCallback(function (b) {
        return b + "/link"
    });
    this.addListener("checkchange", function (e, d) {
        var f = function (k, j) {
            var h = k.childNodes;
            for (var g = 0; g < h.length; g++) {
                h[g].ui.toggleCheck(j);
                h[g].attributes.checked = j;
                f(h[g], j)
            }
        };
        f(e, d);
        var b = function (h, j) {
            if (!h) {
                return
            }
            if (!j) {
                h.ui.toggleCheck(j);
                h.attributes.checked = j
            } else {
                var g = h.childNodes.all(function (k) {
                    return k.ui.isChecked()
                });
                if (g) {
                    h.ui.toggleCheck(true);
                    h.attributes.checked = true
                }
            }
            b(h.parentNode, j)
        };
        b(e.parentNode, d)
    });
    this.setRootNode(new Ext.tree.TreeNode({text: "", draggable: false, expanded: true}));
    this.getRootNode().appendChild(this.visibleRootChild);
    this.getRootNode().addListener("expand", function (b) {
        b.childNodes.each(function (d) {
            this.getLoader().requestData(d, this.onRootLoaded.bind(this))
        }.bind(this))
    }.bind(this))
};
Ext.extend(Signavio.Extensions.ProcessMapCheckTreePanel, Signavio.Extensions.TreePanel, {style: "padding:0px;padding-top:10px;", anchor: "100% -15", cls: "x-tableview-plugin", forceUpdate: true, recordRel: [Signavio.Const.REL.MODEL], onDestroy: function () {
    Signavio.Extensions.ProcessMapCheckTreePanel.superclass.onDestroy.call(this);
    this.getLoader().releaseAllEvents();
    this.purgeListeners()
}, containsNodeWithIdentifier: function (a) {
    for (var b in this.nodeHash) {
        if (this.nodeHash[b].attributes.identifier == a) {
            return true
        }
    }
    return false
}});
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Extensions) {
    Signavio.Extensions = {}
}
Signavio.Extensions.HoverPanel = function (a) {
    a.renderTo = Ext.getBody();
    Signavio.Extensions.HoverPanel.superclass.constructor.apply(this, arguments)
};
Ext.extend(Signavio.Extensions.HoverPanel, Ext.Panel, {border: false, title: "&nbsp;", cls: "x-info", bodyStyle: "margin-left:9px;background:white;", collapsible: true, layout: "border", offsetTop: 28, initComponent: function () {
    Signavio.Extensions.HoverPanel.superclass.initComponent.apply(this, arguments);
    this.addEvents("showfullview", "hidefullview")
}, onRender: function () {
    Signavio.Extensions.HoverPanel.superclass.onRender.apply(this, arguments);
    this.weightElement = this.facade.getExtView("view").el;
    this.facade.getExtView("view").on("resize", this.syncSize.bind(this));
    //this.header.addClass("x-header-info");
    window.setTimeout(this.syncSize.bind(this, true), 100)
}, syncSize: function (d) {
    if (this.syncing) {
        return
    }
    this.syncing = true;
    d = d === true || undefined;
    this.el.setWidth(this.weightElement.getWidth() + 5);
    this.el.setLeft(this.weightElement.getLeft() - 5);
    var b = Ext.getBody().getHeight();
    b -= this.weightElement.getTop() + this.weightElement.getHeight();
    var a = this.weightElement.getHeight() + b;
    a -= this.offsetTop;
    if (this.collapsed) {
        a = 0
    } else {
        if (!this.fullviewShown) {
            a = (this.initialConfig.height || 60)
        }
    }
    var e = Ext.getBody().getHeight() - a;
    if (d && this.el.getHeight() > a) {
        this.el.setY(e, {block: true, callback: function () {
            this.setHeight(a);
            this.el.setHeight(a);
            this.doLayout();
            delete this.syncing
        }.bind(this)})
    } else {
        this.setHeight(a);
        this.el.setHeight(a);
        this.el.setY(e, d);
        this.doLayout();
        delete this.syncing
    }
}, showFullView: function () {
    if (this.fireEvent("showfullview", this) === false || this.collapsed) {
        return
    }
    this.fullviewShown = true;
    this.el.addClass("x-info-fullview");
    this.syncSize(true)
}, hideFullView: function () {
    if (this.fireEvent("hidefullview", this) === false) {
        return
    }
    this.fullviewShown = false;
    this.el.removeClass("x-info-fullview");
    this.syncSize(true)
}, collapse: function () {
    if (this.fullviewShown) {
        this.hideFullView()
    } else {
        this.showFullView();
        return;
        this.el.setBottom(0);
        this.el.setTop("auto");
        this.el.setHeight("auto");
        Signavio.Extensions.HoverPanel.superclass.collapse.apply(this, arguments)
    }
}, afterCollapse: function () {
    Signavio.Extensions.HoverPanel.superclass.afterCollapse.apply(this, arguments);
    this.syncSize()
}, expand: function () {
    return;
    this.el.setTop("auto");
    this.el.setBottom(0);
    this.el.setHeight("auto");
    Signavio.Extensions.HoverPanel.superclass.expand.apply(this)
}, afterExpand: function () {
    Signavio.Extensions.HoverPanel.superclass.afterExpand.apply(this, arguments);
    this.syncSize()
}});
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Extensions) {
    Signavio.Extensions = {}
}
new function () {
    Signavio.Extensions.ViewSelectionWindow = function (a, d, b) {
        this.views = a;
        this.modelId = d;
        this.suffix = b;
        if (!(a instanceof Array) || a.length === 0) {
            this.onSubmit();
            return
        }
        Signavio.Extensions.ViewSelectionWindow.superclass.constructor.apply(this, [
            {}
        ]);
        this.show()
    };
    Ext.extend(Signavio.Extensions.ViewSelectionWindow, Ext.Window, {resizable: false, minimizable: false, modal: true, width: 345, defaultButton: 0, title: Signavio.I18N.Repository.Offer.WindowTitle, bodyStyle: "background-color:white;", layout: "form", buttons: [
        {text: Signavio.I18N.Repository.Offer.WindowBtnExport, handler: function () {
            this.ownerCt.onSubmit();
            this.ownerCt.close()
        }},
        {text: Signavio.I18N.Repository.Offer.WindowBtnCancel, handler: function () {
            this.ownerCt.close()
        }}
    ], getCurrentViewId: function () {
        if (!this.items) {
            return this.modelId
        }
        var a = this.items.items.find(function (b) {
            return b.checked
        });
        if (a && a.href) {
            return a.href
        } else {
            return this.modelId
        }
    }, onSubmit: function () {
        window.open(Signavio.Config.BACKEND_PATH + this.getCurrentViewId() + this.suffix)
    }, initComponent: function () {
        var a = Ext.id();
        this.items = this.views.map(function (b) {
            return new Ext.form.Radio({ctCls: "y-export-radio", name: a, hideLabel: true, boxLabel: b.rep.name, href: b.href.replace("/info", "")})
        });
        this.items.unshift(new Ext.form.Radio({ctCls: "y-export-radio-original", name: a, hideLabel: true, boxLabel: Signavio.I18N.Repository.Revision.resetView, href: this.modelId, checked: true}));
        this.items.unshift(new Ext.form.Label({cls: "y-export-window-description", text: Signavio.I18N.Repository.Offer.WindowDescription}));
        Signavio.Extensions.ViewSelectionWindow.superclass.initComponent.call(this)
    }})
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Log) {
    Signavio.Log = {}
}
if (!Signavio.Log.Level) {
    Signavio.Log.Level = {}
}
new function () {
    var a = [];
    Signavio.Log.register = function (b) {
        if (b instanceof Function && !a.include(b)) {
            a.push(b)
        }
    };
    Signavio.Log.doLog = function (f, b) {
        f = f === undefined ? Signavio.Log.Level.WARNING : f;
        if (f >= Signavio.Config.LOGLEVELSHOWN) {
            a.each(function (e) {
                e.call(e, f, b)
            })
        }
        if (f < Signavio.Config.LOGLEVEL) {
            return
        }
        b = b instanceof Object ? b.message : b;
        try {
            if (window.console && typeof window.console.log === "function") {
                console.log(Signavio.I18N.Repository.Log.LOG + ": " + f + " - " + b)
            }
        } catch (d) {
        }
        if (f >= Signavio.Log.Level.ERROR) {
            switch (f) {
                case Signavio.Log.Level.ERROR:
                    throw Signavio.I18N.Repository.Log.ERROR + ": " + f + " - " + b;
                    break;
                case Signavio.Log.Level.CRITICAL:
                    throw Signavio.I18N.Repository.Log.CRITICAL + ": " + f + " - " + b;
                    break;
                case Signavio.Log.Level.FATAL:
                    throw Signavio.I18N.Repository.Log.FATAL + ": " + f + " - " + b;
                    break;
                default:
                    throw Signavio.I18N.Repository.Log.LOG + ": " + f + " - " + b
            }
        }
    };
    Signavio.Log.doFatal = function (b) {
        Signavio.Log.doLog(Signavio.Log.Level.FATAL, b)
    };
    Signavio.Log.doCritical = function (b) {
        Signavio.Log.doLog(Signavio.Log.Level.CRITICAL, b)
    };
    Signavio.Log.doError = function (b) {
        Signavio.Log.doLog(Signavio.Log.Level.ERROR, b)
    };
    Signavio.Log.doWarning = function (b) {
        Signavio.Log.doLog(Signavio.Log.Level.WARNING, b)
    };
    Signavio.Log.doNotice = function (b) {
        Signavio.Log.doLog(Signavio.Log.Level.NOTICE, b)
    };
    Signavio.Log.doInfo = function (b) {
        Signavio.Log.doLog(Signavio.Log.Level.INFO, b)
    };
    Signavio.Log.doDebug = function (b) {
        Signavio.Log.doLog(Signavio.Log.Level.DEBUG, b)
    };
    Signavio.Log.Level.FATAL = 6;
    Signavio.Log.Level.CRITICAL = 5;
    Signavio.Log.Level.ERROR = 4;
    Signavio.Log.Level.WARNING = 3;
    Signavio.Log.Level.NOTICE = 2;
    Signavio.Log.Level.INFO = 1;
    Signavio.Log.Level.DEBUG = 0
}();
var log = Signavio.Log.doLog;
var error = Signavio.Log.doError;
var warn = Signavio.Log.doWarning;
var notice = Signavio.Log.doNotice;
var info = Signavio.Log.doNotice;
var Clazz = function () {
};
Clazz.prototype.construct = function () {
};
Clazz.extend = function (f) {
    var a = function () {
        if (arguments[0] !== Clazz) {
            this.construct.apply(this, arguments)
        }
    };
    var e = new this(Clazz);
    var b = this.prototype;
    for (var g in f) {
        var d = f[g];
        if (d instanceof Function) {
            d.$ = b
        }
        e[g] = d
    }
    a.prototype = e;
    a.extend = this.extend;
    return a
};
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Helper) {
    Signavio.Helper = {}
}
new function () {
    Signavio.Helper.isIE6 = function () {
        return window.navigator.userAgent.toLowerCase().indexOf("msie 6.") >= 0
    };
    Signavio.Helper.isIE7 = function () {
        return window.navigator.userAgent.toLowerCase().indexOf("msie 7.") >= 0
    };
    Signavio.Helper.isIE8 = function () {
        return window.navigator.userAgent.toLowerCase().indexOf("msie 8.") >= 0
    };
    Signavio.Helper.getBrowserVersion = function () {
        var j = window.navigator.userAgent.toLowerCase();
        var h = "";
        if (Ext.isIE) {
            h = window.navigator.userAgent.toLowerCase().match(/msie.+?;/g).first().replace(/[^0-9\.]/g, "")
        } else {
            h = window.navigator.userAgent.toLowerCase().split(/\//g).last().split(/\s/g).first().replace(/[^0-9\.]/g, "")
        }
        h = h.match(/[0-9]+\.[0-9]+/).first();
        return h
    };
    Signavio.Helper.SimulateEvent = function (k, j) {
        j = j || "click";
        if (document.createEvent) {
            var h = document.createEvent("MouseEvents");
            h.initMouseEvent(j, true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
            return k.dispatchEvent(h)
        } else {
            if (document.createEventObject) {
                var h = document.createEventObject();
                h.detail = 0;
                h.screenX = 0;
                h.screenY = 0;
                h.clientX = 0;
                h.clientY = 0;
                h.ctrlKey = false;
                h.altKey = false;
                h.shiftKey = false;
                h.metaKey = false;
                h.button = 0;
                h.relatedTarget = null;
                return k.fireEvent("on" + j, h)
            }
        }
    };
    Signavio.Helper.OpenWindow = function (k, j) {
        var h = Ext.get(document.createElement("a"));
        h.on("click", function () {
            var l = window.open(k);
            if (j instanceof Function) {
                j(l)
            }
            h.remove()
        });
        Ext.getBody().appendChild(h);
        Signavio.Helper.SimulateEvent(h.dom)
    };
    Signavio.Helper.CutOpenID = function (n, m) {
        var l = ["blogspot.com", "verisignlabs.com"];
        var h = new Ext.Template('<span title="{openid}">{cuttedopenid}</span>');
        m = m || 30;
        var k = n.length >= m;
        if (k) {
            var j = l.some(function (q) {
                return n.toLowerCase().include(q)
            });
            var p = j ? n.slice(0, m) + "..." : "..." + n.slice(n.length - m)
        }
        return k ? h.apply({openid: n, cuttedopenid: p}) : n
    };
    var f = ["i", "I", "j", "l"];
    var e = ["a", "b", "d", "e", "f", "J", "k", "n", "o", "r", "s", "t", "u", "v", "y", "z"];
    var d = ["%", "W@", "mwMOQÖ#+=<>~^", "DGCHNRUÜ&AÄ", "BKSVXZbdghnopquxöüETY1234567890ß_§$*´`µ€vyPaeäF", "{}kL?°²³cszJ", '"-rt/()[]:;|\\!fI.,', "j ", "'il"];
    var b = [11, 10, 8, 7, 6, 5, 4, 3, 2];
    var g = 11;
    var a = function (j) {
        var h = d.find(function (k) {
            return k.include(j)
        });
        return Math.ceil((h ? b[d.indexOf(h)] : 9) * (g / 11))
    };
    Signavio.Helper.TruncateByWidth = function (m, k, l) {
        m = m || "";
        if (typeof k != "number") {
            return m
        }
        var j = 0;
        var h = 0;
        while (j < k && h < m.length) {
            j += a(m.charAt(h++))
        }
        while ((j + 14) > k && h > 0) {
            j -= a(m.charAt(h--))
        }
        return h == m.length ? m : m.slice(0, h - 1) + (l !== undefined ? l : "&hellip;")
    };
    Signavio.Helper.SplitIntoTwoLines = function (l, j) {
        if (!l) {
            return""
        }
        var k = Signavio.Helper.TruncateByWidth(l, (j / 2) + 16, "");
        if (k === l) {
            return l
        }
        var h = l.slice(k.length);
        h = Signavio.Helper.TruncateByWidth(h, j / 2, "<small>&hellip;</small>");
        return k + "<br/>" + h
    };
    Signavio.Helper.ParseDate = function (h) {
        var j = h;
        if (typeof h == "string") {
            h = h.gsub("-", "/");
            h = h.replace(/\.[0-9]+/g, "");
            h = new Date(h);
            if (!(h instanceof Date) || isNaN(h.valueOf())) {
                h = Date.parseDate(j, "D M j G:i:s T Y")
            }
        }
        return h
    };
    Signavio.Helper.BeautifyDate = function (j, m) {
        if (j === undefined) {
            return""
        }
        j = Signavio.Helper.ParseDate(j);
        var h = new Date();
        var l = new Date(j.getElapsed(h));
        var n = {};
        n.year = l.getUTCFullYear() - 1970;
        n.month = l.getUTCMonth();
        n.days = l.getUTCDate() - 1;
        n.hours = l.getUTCHours();
        n.minutes = l.getUTCMinutes();
        n.seconds = l.getUTCSeconds();
        var k = "";
        if (n.year) {
            k += n.year + " " + (n.year > 1 ? Signavio.I18N.Repository.Date.years : Signavio.I18N.Repository.Date.year)
        } else {
            if (n.month) {
                k += n.month + " " + (n.month > 1 ? Signavio.I18N.Repository.Date.months : Signavio.I18N.Repository.Date.month)
            } else {
                if (n.days) {
                    k += n.days + " " + (n.days > 1 ? Signavio.I18N.Repository.Date.days : Signavio.I18N.Repository.Date.day)
                } else {
                    if (n.hours) {
                        k += n.hours + " " + (n.hours > 1 ? Signavio.I18N.Repository.Date.hours : Signavio.I18N.Repository.Date.hour)
                    } else {
                        if (n.minutes) {
                            k += n.minutes + " " + (n.minutes > 1 ? Signavio.I18N.Repository.Date.minutes : Signavio.I18N.Repository.Date.minute)
                        } else {
                            if (n.seconds) {
                                k += n.seconds + " " + (n.seconds > 1 ? Signavio.I18N.Repository.Date.seconds : Signavio.I18N.Repository.Date.second)
                            }
                        }
                    }
                }
            }
        }
        return m === true ? k : "<span title='" + j.format(Signavio.Const.DATE_FORMAT) + "'>" + k + "</span>"
    };
    Signavio.Helper.LoadRelatedInfo = function (j, h) {
    };
    Signavio.Helper.GetAllCountries = function () {
        return[
            {id: "AF", name: "Afghanistan"},
            {id: "AL", name: "Albania"},
            {id: "DZ", name: "Algeria"},
            {id: "AS", name: "American Samoa"},
            {id: "AD", name: "Andorra"},
            {id: "AI", name: "Anguilla"},
            {id: "AQ", name: "Antarctica"},
            {id: "AG", name: "Antigua And Barbuda"},
            {id: "AR", name: "Argentina"},
            {id: "AM", name: "Armenia"},
            {id: "AW", name: "Aruba"},
            {id: "AU", name: "Australia"},
            {id: "AT", name: "Austria"},
            {id: "AZ", name: "Ayerbaijan"},
            {id: "BS", name: "Bahamas, The"},
            {id: "BH", name: "Bahrain"},
            {id: "BD", name: "Bangladesh"},
            {id: "BB", name: "Barbados"},
            {id: "BY", name: "Belarus"},
            {id: "BZ", name: "Belize"},
            {id: "BE", name: "Belgium"},
            {id: "BJ", name: "Benin"},
            {id: "BM", name: "Bermuda"},
            {id: "BT", name: "Bhutan"},
            {id: "BO", name: "Bolivia"},
            {id: "BV", name: "Bouvet Is"},
            {id: "BA", name: "Bosnia and Herzegovina"},
            {id: "BW", name: "Botswana"},
            {id: "BR", name: "Brazil"},
            {id: "IO", name: "British Indian Ocean Territory"},
            {id: "BN", name: "Brunei"},
            {id: "BG", name: "Bulgaria"},
            {id: "BF", name: "Burkina Faso"},
            {id: "BI", name: "Burundi"},
            {id: "KH", name: "Cambodia"},
            {id: "CM", name: "Cameroon"},
            {id: "CA", name: "Canada"},
            {id: "CV", name: "Cape Verde"},
            {id: "KY", name: "Cayman Is"},
            {id: "CF", name: "Central African Republic"},
            {id: "TD", name: "Chad"},
            {id: "CL", name: "Chile"},
            {id: "CN", name: "China"},
            {id: "HK", name: "Hong Kong"},
            {id: "MO", name: "Macau S.A.R."},
            {id: "CX", name: "Christmas Is"},
            {id: "CC", name: "Cocos (Keeling) Is"},
            {id: "CO", name: "Colombia"},
            {id: "KM", name: "Comoros"},
            {id: "CK", name: "Cook Islands"},
            {id: "CR", name: "Costa Rica"},
            {id: "CI", name: "Cote D'Ivoire (Ivory Coast)"},
            {id: "HR", name: "Croatia (Hrvatska)"},
            {id: "CY", name: "Cyprus"},
            {id: "CZ", name: "Czech Republic"},
            {id: "CD", name: "Democratic Republic of the Congo"},
            {id: "DK", name: "Denmark"},
            {id: "DM", name: "Dominica"},
            {id: "DO", name: "Dominican Republic"},
            {id: "DJ", name: "Djibouti"},
            {id: "TP", name: "East Timor"},
            {id: "EC", name: "Ecuador"},
            {id: "EG", name: "Egypt"},
            {id: "SV", name: "El Salvador"},
            {id: "GQ", name: "Equatorial Guinea"},
            {id: "ER", name: "Eritrea"},
            {id: "EE", name: "Estonia"},
            {id: "ET", name: "Ethiopia"},
            {id: "FK", name: "Falkland Is (Is Malvinas)"},
            {id: "FO", name: "Faroe Islands"},
            {id: "FJ", name: "Fiji Islands"},
            {id: "FI", name: "Finland"},
            {id: "FR", name: "France"},
            {id: "GF", name: "French Guiana"},
            {id: "PF", name: "French Polynesia"},
            {id: "TF", name: "French Southern Territories"},
            {id: "MK", name: "F.Y.R.O. Macedonia"},
            {id: "GA", name: "Gabon"},
            {id: "GM", name: "Gambia, The"},
            {id: "GE", name: "Georgia"},
            {id: "DE", name: "Germany"},
            {id: "GH", name: "Ghana"},
            {id: "GI", name: "Gibraltar"},
            {id: "GR", name: "Greece"},
            {id: "GL", name: "Greenland"},
            {id: "GD", name: "Grenada"},
            {id: "GP", name: "Guadeloupe"},
            {id: "GU", name: "Guam"},
            {id: "GT", name: "Guatemala"},
            {id: "GN", name: "Guinea"},
            {id: "GW", name: "Guinea-Bissau"},
            {id: "GY", name: "Guyana"},
            {id: "HT", name: "Haiti"},
            {id: "HM", name: "Heard and McDonald Is"},
            {id: "HN", name: "Honduras"},
            {id: "HU", name: "Hungary"},
            {id: "IS", name: "Iceland"},
            {id: "IN", name: "India"},
            {id: "ID", name: "Indonesia"},
            {id: "IE", name: "Ireland"},
            {id: "IL", name: "Israel"},
            {id: "IT", name: "Italy"},
            {id: "JM", name: "Jamaica"},
            {id: "JP", name: "Japan"},
            {id: "JO", name: "Jordan"},
            {id: "KZ", name: "Kayakhstan"},
            {id: "KE", name: "Kenya"},
            {id: "KI", name: "Kiribati"},
            {id: "KR", name: "Korea, South"},
            {id: "KW", name: "Kuwait"},
            {id: "KG", name: "Kyrgyzstan"},
            {id: "LA", name: "Laos"},
            {id: "LV", name: "Latvia"},
            {id: "LB", name: "Lebanon"},
            {id: "LS", name: "Lesotho"},
            {id: "LR", name: "Liberia"},
            {id: "LI", name: "Liechtenstein"},
            {id: "LT", name: "Lithuania"},
            {id: "LU", name: "Luxembourg"},
            {id: "MG", name: "Madagascar"},
            {id: "MW", name: "Malawi"},
            {id: "MY", name: "Malaysia"},
            {id: "MV", name: "Maldives"},
            {id: "ML", name: "Mali"},
            {id: "MT", name: "Malta"},
            {id: "MH", name: "Marshall Is"},
            {id: "MR", name: "Mauritania"},
            {id: "MU", name: "Mauritius"},
            {id: "MQ", name: "Martinique"},
            {id: "YT", name: "Mayotte"},
            {id: "MX", name: "Mexico"},
            {id: "FM", name: "Micronesia"},
            {id: "MD", name: "Moldova"},
            {id: "MC", name: "Monaco"},
            {id: "MN", name: "Mongolia"},
            {id: "MS", name: "Montserrat"},
            {id: "MA", name: "Morocco"},
            {id: "MZ", name: "Mozambique"},
            {id: "MM", name: "Myanmar"},
            {id: "NA", name: "Namibia"},
            {id: "NR", name: "Nauru"},
            {id: "NP", name: "Nepal"},
            {id: "NL", name: "Netherlands, The"},
            {id: "AN", name: "Netherlands Antilles"},
            {id: "NC", name: "New Caledonia"},
            {id: "NZ", name: "New Zealand"},
            {id: "NI", name: "Nicaragua"},
            {id: "NE", name: "Niger"},
            {id: "NG", name: "Nigeria"},
            {id: "NU", name: "Niue"},
            {id: "NO", name: "Norway"},
            {id: "NF", name: "Norfolk Island"},
            {id: "MP", name: "Northern Mariana Is"},
            {id: "OM", name: "Oman"},
            {id: "PK", name: "Pakistan"},
            {id: "PW", name: "Palau"},
            {id: "PA", name: "Panama"},
            {id: "PG", name: "Papua new Guinea"},
            {id: "PY", name: "Paraguay"},
            {id: "PE", name: "Peru"},
            {id: "PH", name: "Philippines"},
            {id: "PN", name: "Pitcairn Island"},
            {id: "PL", name: "Poland"},
            {id: "PT", name: "Portugal"},
            {id: "PR", name: "Puerto Rico"},
            {id: "QA", name: "Qatar"},
            {id: "CG", name: "Republic of the Congo"},
            {id: "RE", name: "Reunion"},
            {id: "RO", name: "Romania"},
            {id: "RU", name: "Russia"},
            {id: "SH", name: "Saint Helena"},
            {id: "KN", name: "Saint Kitts And Nevis"},
            {id: "LC", name: "Saint Lucia"},
            {id: "PM", name: "Saint Pierre and Miquelon"},
            {id: "VC", name: "Saint Vincent And The Grenadines"},
            {id: "WS", name: "Samoa"},
            {id: "WM", name: "San Marino"},
            {id: "ST", name: "Sao Tome and Principe"},
            {id: "SA", name: "Saudi Arabia"},
            {id: "SN", name: "Senegal"},
            {id: "SC", name: "Seychelles"},
            {id: "SL", name: "Sierra Leone"},
            {id: "SG", name: "Singapore"},
            {id: "SK", name: "Slovakia"},
            {id: "SI", name: "Slovenia"},
            {id: "SB", name: "Solomon Islands"},
            {id: "SO", name: "Somalia"},
            {id: "ZA", name: "South Africa"},
            {id: "GS", name: "South Georgia & The S. Sandwich Is"},
            {id: "ES", name: "Spain"},
            {id: "LK", name: "Sri Lanka"},
            {id: "SR", name: "Suriname"},
            {id: "SJ", name: "Svalbard And Jan Mayen Is"},
            {id: "SZ", name: "Swaziland"},
            {id: "SE", name: "Sweden"},
            {id: "CH", name: "Switzerland"},
            {id: "SY", name: "Syria"},
            {id: "TW", name: "Taiwan"},
            {id: "TJ", name: "Tajikistan"},
            {id: "TZ", name: "Tanzania"},
            {id: "TH", name: "Thailand"},
            {id: "TL", name: "Timor-Leste"},
            {id: "TG", name: "Togo"},
            {id: "TK", name: "Tokelau"},
            {id: "TO", name: "Tonga"},
            {id: "TT", name: "Trinidad And Tobago"},
            {id: "TN", name: "Tunisia"},
            {id: "TR", name: "Turkey"},
            {id: "TC", name: "Turks And Caicos Is"},
            {id: "TM", name: "Turkmenistan"},
            {id: "TV", name: "Tuvalu"},
            {id: "UG", name: "Uganda"},
            {id: "UA", name: "Ukraine"},
            {id: "AE", name: "United Arab Emirates"},
            {id: "GB", name: "United Kingdom"},
            {id: "US", name: "United States"},
            {id: "UM", name: "United States Minor Outlying Is"},
            {id: "UY", name: "Uruguay"},
            {id: "UZ", name: "Uzbekistan"},
            {id: "VU", name: "Vanuatu"},
            {id: "VA", name: "Vatican City State (Holy See)"},
            {id: "VE", name: "Venezuela"},
            {id: "VN", name: "Vietnam"},
            {id: "VG", name: "Virgin Islands (British)"},
            {id: "VI", name: "Virgin Islands (US)"},
            {id: "WF", name: "Wallis And Futuna Islands"},
            {id: "EH", name: "Western Sahara"},
            {id: "YE", name: "Yemen"},
            {id: "ZM", name: "Zambia"},
            {id: "ZW", name: "Zimbabwe"}
        ].sort(function (j, h) {
            return j.name > h.name ? 1 : -1
        })
    };
    Signavio.Helper.BusinessObjectSortingFunction = function (l, j) {
        if (!l || !j) {
            return 0
        }
        try {
            var p = l.get("rel").toLowerCase();
            var n = j.get("rel").toLowerCase();
            if (p == n) {
                var k = (l.get("rep").name || l.get("rep").title || l.get("rep").username || "").toLowerCase();
                var h = (j.get("rep").name || j.get("rep").title || j.get("rep").username || "").toLowerCase();
                k = k.gsub("ä", "a").gsub("ö", "o").gsub("ü", "u").trim();
                h = h.gsub("ä", "a").gsub("ö", "o").gsub("ü", "u").trim();
                return(k < h ? -1 : (k > h ? 1 : 0))
            } else {
                return(p < n ? -1 : 1)
            }
        } catch (m) {
            return 0
        }
    };
    Signavio.Helper.HashUtils = {callbacks: [], interval: 100, useIFrame: Ext.isIE && (Signavio.Helper.isIE6() || Signavio.Helper.isIE7()), getIFrameDoc: function () {
        if (!this.iframe) {
            var h;
            this.iframe = h = document.createElement("iframe");
            h.style.display = "none";
            Ext.getBody().dom.appendChild(h)
        }
        return this.iframe.contentWindow.document
    }, getHash: function () {
        var h = window.location.href.indexOf("#");
        return(h == -1 ? "" : window.location.href.substr(h + 1))
    }, set: function (m, j) {
        if (this.useIFrame && !j) {
            try {
                var l = this.getIFrameDoc();
                l.open();
                l.write("<html><body>" + m + "</body></html>");
                l.close()
            } catch (k) {
                window.setTimeout(this.set.bind(this, m, j), 10)
            }
        } else {
            if (j === true) {
                var h = window.location.toString().replace(/#.*/, "");
                window.location.replace(h + "#" + m)
            } else {
                window.location.hash = (!m.startsWith("#") ? "#" : "") + m
            }
        }
    }, get: function () {
        if (this.useIFrame) {
            try {
                return this.getIFrameDoc().body.innerText
            } catch (h) {
                return this.getHash()
            }
        } else {
            return window.location.hash
        }
    }, onChange: function (h) {
        this.callbacks.push(h);
        if (this.useIFrame) {
            this.startWithIFrame(this.interval)
        } else {
            this.start(this.interval)
        }
    }, startWithIFrame: function (h) {
        try {
            this.getIFrameDoc()
        } catch (k) {
            window.setTimeout(this.setIFrame.bind(this), 10);
            return
        }
        if (this.pe) {
            return false
        }
        var j = this.getHash();
        this.set(j);
        this.pe = new PeriodicalExecuter(function () {
            try {
                var l = this.getIFrameDoc().body.innerText;
                if (l !== j) {
                    window.location.hash = j = l;
                    this.callbacks.each(function (p) {
                        p(l)
                    }.bind(this))
                } else {
                    var n = this.getHash();
                    if (j !== n) {
                        this.set(n);
                        j = n
                    }
                }
            } catch (m) {
            }
        }.bind(this), (h || this.interval) / 1000)
    }, start: function (h) {
        if (this.pe) {
            return false
        }
        var j = this.get();
        this.pe = new PeriodicalExecuter(function () {
            if (j != this.get()) {
                j = window.location.hash;
                this.callbacks.each(function (k) {
                    k(this.getParams())
                }.bind(this))
            }
        }.bind(this), (h || this.interval) / 1000)
    }, stop: function () {
        if (this.pe) {
            this.pe.stop();
            delete this.pe
        }
    }, getParams: function () {
        return(window.location.hash + "").slice(1)
    }, toString: function () {
        return this.getParams()
    }}
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Core) {
    Signavio.Core = {}
}
new function () {
    Signavio.Core.Plugin = {neededStores: [], enabled: false, construct: function (a) {
        arguments.callee.$.construct.apply(this, arguments);
        this.facade = a;
        this._selection = [];
        this.facade.registerOnEvent("selectionchange", function (b, d) {
            this._selection = (d || []).uniq()
        }.bind(this));
        this.facade.registerOnEvent("finialize", this.finialize.bind(this));
        this.isInitializing = true;
        this.facade.registerOnEvent("finialize", function () {
            this.isInitializing = false
        }.bind(this))
    }, getSelection: function () {
        return this._selection || []
    }, finialize: function () {
    }, confirm: function (a, b) {
        Ext.Msg.confirm("Signavio", a, function (d, e) {
            if (d == "yes") {
                b()
            }
        }.bind(this))
    }, selectionNotInTrash: function () {
        return !this.selectionInTrash()
    }, selectionInTrash: function () {
        return this.facade.getCurrentRootFolder() !== undefined && this.facade.getCurrentRootFolder() === this.facade.getTrashRecord()
    }, selectionInSearch: function () {
        try {
            return Signavio.Core.StoreManager.getChildEntitiesStore().getIdentifier() === Signavio.Config.SEARCH_PATH
        } catch (a) {
            return false
        }
    }, selectionIsJbpm4: function () {
        return this.selectionIs("http://b3mn.org/stencilset/jbpm4#")
    }, selectionIsTim: function () {
        return this.selectionIs("http://b3mn.org/stencilset/timjpdl3#")
    }, selectionIsEpc: function () {
        return this.selectionIs("http://b3mn.org/stencilset/epc#")
    }, selectionIsBpmn11: function () {
        return this.selectionIs("http://b3mn.org/stencilset/bpmn1.1#")
    }, selectionIsBpmn20: function () {
        return this.selectionIs("http://b3mn.org/stencilset/bpmn2.0#")
    }, selectionIs: function (a) {
        return this.getSelection().all(function (b) {
            return b.get("rep").namespace == a
        })
    }, selectionIsEpcOrBpmn20: function () {
        return this.selectionIsIn(["http://b3mn.org/stencilset/epc#", "http://b3mn.org/stencilset/bpmn2.0#"])
    }, selectionIsIn: function (a) {
        return this.getSelection().all(function (b) {
            return a.any(function (d) {
                return b.get("rep").namespace == d
            })
        })
    }, oneSelected: function () {
        return this.getSelection().length === 1 && this.getSelection()[0].get("rel") == "mod"
    }, manySelected: function () {
        return this.getSelection().length >= 1 && this.getSelection().all(function (a) {
            return a.get("rel") == "mod"
        })
    }, noneSelected: function () {
        return this.getSelection().length <= 0
    }, oneDirectorySelected: function () {
        return this.getSelection().length === 1 && this.getSelection()[0].get("rel") === "dir"
    }};
    Signavio.Core.Plugin = Clazz.extend(Signavio.Core.Plugin)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Core) {
    Signavio.Core = {}
}
new function () {
    Signavio.Core.ComponentPlugin = {panel: null, parent: null, renderToView: null, title: null, construct: function (a) {
        arguments.callee.$.construct.apply(this, arguments);
        this.parent = this.facade.getExtView(this.renderToView);
        this._doRender()
    }, _doRender: function () {
        this.beforeRender();
        this.render();
        if (this.parent && this.panel && (this.panel instanceof Ext.Container || this.panel instanceof Ext.BoxComponent)) {
            if (this.renderToView === "hover") {
                this.parent.items.push(this)
            } else {
                this.parent.add(this.panel);
                if (!(this.parent instanceof Ext.Toolbar)) {
                    this.parent.doLayout()
                }
                this.afterRender()
            }
            if (!this.enabled) {
                this.panel.hide()
            }
        }
    }, beforeRender: function () {
    }, render: function (a) {
    }, afterRender: function () {
    }, deleteChildItems: function (a) {
        if (!a) {
            a = this.panel
        }
        if (a && a.items) {
            a.items.each(function (b) {
                a.remove(b)
            }.bind(this))
        }
    }};
    Signavio.Core.ComponentPlugin = Signavio.Core.Plugin.extend(Signavio.Core.ComponentPlugin)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Core) {
    Signavio.Core = {}
}
new function () {
    var b = {};
    new function () {
        var d = [];
        var f = [];
        var e = [];
        var h = null;
        var g = false;
        b.register = function (k, j) {
            if (!g) {
                j.registerOnEvent("selectionchange", b.set.bind(b));
                g = true
            }
            if (!e.include(k)) {
                e.push(k)
            }
        };
        b.callOnce = function (j) {
            j = j
        };
        b.set = function (l, j, k) {
            if (l && l === "selectionchange") {
                f = d;
                d = j;
                e.each(function (m) {
                    if (m instanceof Function) {
                        m.apply(m, [d, f])
                    }
                })
            }
        };
        b.get = function () {
            return d
        }
    }();
    var a = {};
    new function () {
        var d = [];
        var e;
        a.registerOnChange = function (f, g) {
            d.push({types: f, callback: g})
        };
        a.deleteStoreListeners = function (f) {
            var g = f.map(function (h) {
                return h && h.get("href")
            }).compact();
            g.each(function (j) {
                var h = Signavio.Core.StoreManager.getRelatedStore(null, j);
                h.purgeListeners()
            })
        };
        a.call = function (f) {
            d.each(function (j) {
                var h = j.types;
                var g = $H({});
                f.each(function (l) {
                    var k = [];
                    l.value.each(function (m) {
                        if (h.include(m.get("rel"))) {
                            k.push(m)
                        }
                    });
                    g.set(l.key, k.reduce())
                });
                j.callback.apply(j.callback, [g, f])
            })
        };
        a.set = function (f, g) {
            a.deleteStoreListeners(g);
            window.clearTimeout(e);
            if (!f || f.size() <= 0) {
                e = window.setTimeout(a.call.bind(this, $H({}), []), 1);
                return
            }
            e = window.setTimeout(function () {
                var j = $H({});
                var h = f.compact().map(function (k) {
                    return k.get("href")
                });
                h.each(function (m) {
                    var l = function (n) {
                        j.set(m, n);
                        a.call(j)
                    }.bind(this);
                    var k = Signavio.Core.StoreManager.getRelatedStore(m, m);
                    k.on("load", l);
                    k.on("update", l)
                }.bind(this))
            }.bind(this), 10)
        }
    }();
    Signavio.Core.ContextPlugin = {relTypes: [], construct: function () {
        arguments.callee.$.construct.apply(this, arguments);
        if (this.relTypes && this.relTypes.length > 0) {
            a.registerOnChange(this.relTypes, this.initUpdate.bind(this));
            b.register(a.set, this.facade)
        }
        window.setTimeout(this.initUpdate.bind(this, $H({}), $H({})), 1)
    }, initUpdate: function (e, d) {
        this.currentRecordsSet = e;
        this.currentStores = d ? d.values() : [];
        this.update.apply(this, arguments);
        if (this.panel && this.panel.setTitle instanceof Function && this.panel.initialConfig.header !== false) {
            var f = e.keys().length;
            var h = f === 1 ? d.values()[0].getRecords("info")[0] : null;
            var g = h ? h.get("rep").title || h.get("rep").name : f + " " + Signavio.I18N.Repository.ContextPlugin.selectedElements;
            this.panel.doLayout()
        }
    }, update: function (d, e) {
    }};
    Signavio.Core.ContextPlugin = Signavio.Core.ComponentPlugin.extend(Signavio.Core.ContextPlugin)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Core) {
    Signavio.Core = {}
}
new function () {
    Signavio.Core.ExportPlugin = {enabled: true, construct: function () {
        arguments.callee.$.construct.apply(this, arguments)
    }, doExport: function (e) {
        var d = this.getSelection();
        var a = this.currentRecordsSet.values()[0].get("rep");
        var b = new Signavio.Extensions.ViewSelectionWindow(a, d[0].get("href"), e)
    }};
    Signavio.Core.ExportPlugin = Signavio.Core.ContextPlugin.extend(Signavio.Core.ExportPlugin)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Core) {
    Signavio.Core = {}
}
new function () {
    var a = $H({});
    a.activateGroup = function (g, b, d) {
        var f;
        try {
            f = typeof d == "number" ? this.keys()[d] : d
        } catch (h) {
            return
        }
        if (this.get(f) instanceof Array) {
            if (g && g.items) {
                g.items.each(function (e) {
                    e.hide()
                })
            }
            this.get(f).each(function (e) {
                if (g.items.contains(e.panel)) {
                    e.panel.show()
                } else {
                    g.add(e.panel);
                    g.doLayout();
                    e.afterRender()
                }
            })
        }
    };
    a.registerOnViewSwitch = function (d, b) {
        if (!this.registeredOnEvent) {
            b.registerOnEvent("viewswitch", d);
            this.registeredOnEvent = true
        }
    };
    Signavio.Core.ViewPlugin = {renderToView: "view", viewGroup: null, viewIcons: {icon: Signavio.Config.EXPLORER_PATH + "/src/img/nuvola/16x16/actions/view_icon.png", table: Signavio.Config.EXPLORER_PATH + "/src/img/nuvola/16x16/actions/view_text.png"}, _doRender: function () {
        this.beforeRender();
        this.render();
        if (this.enabled && this.parent && this.panel && (this.panel instanceof Ext.Container || this.panel instanceof Ext.BoxComponent)) {
            var b = this.viewGroup;
            if (!(b instanceof Array)) {
                b = [b]
            }
            b.each(function (d) {
                if (!a.get(d)) {
                    a.set(d, [])
                }
                a.get(d).push(this)
            }.bind(this));
            a.registerOnViewSwitch(a.activateGroup.bind(a, this.parent), this.facade);
            this.facade.registerOnEvent("viewswitch", function () {
                if (this.panel && this.panel.rendered && this.isViewSelected()) {
                    this.onViewSelect()
                }
            }.bind(this))
        }
    }, isViewSelected: function () {
        return this.parent.items.contains(this.panel) && this.panel.isVisible()
    }, onViewSelect: function () {
    }};
    Signavio.Core.ViewPlugin = Signavio.Core.ContextPlugin.extend(Signavio.Core.ViewPlugin)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Core) {
    Signavio.Core = {}
}
new function () {
    var a = [];
    Signavio.Core.AdministrationPlugins = {construct: function () {
        arguments.callee.$.construct.apply(this, arguments);
        a.push(this)
    }, generateTab: function () {
        return null
    }, getAllTabs: function () {
        var b = [];
        a.each(function (e) {
            var d = e.generateTab();
            if (d && "undefined" != typeof e.index) {
                if (d instanceof Array) {
                    d.each(function (f) {
                        f.index = e.index
                    })
                } else {
                    d.index = e.index
                }
            }
            b = b.concat(d || [])
        });
        return b.flatten()
    }, doClose: function () {
    }, doOpen: function () {
    }, showTab: function (b) {
        b = b || 0;
        var d;
        a.each(function (e) {
            d = e.doOpen(b) || d
        });
        return d
    }, close: function () {
        a.each(function (b) {
            b.doClose()
        })
    }};
    Signavio.Core.AdministrationPlugins = Signavio.Core.Plugin.extend(Signavio.Core.AdministrationPlugins)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Templates) {
    Signavio.Templates = {}
}
//Signavio.Templates.header = new Ext.XTemplate('<div id="signavio_repository_header">', '<a href="{[Signavio.Config.HEADER_LOGO_LINK_TARGET]}" target="_blank">', '<img src="..' + Signavio.Config.LIBS_PATH + '/ext-2.0.2/resources/images/default/s.gif" id="signavio_repository_logo" alt="Signavio" title="Signavio"/>', "</a>", '<tpl if="isTestSystem"><span style="font-size:20px;font-weight:bold;color:white;padding-left:50px"> WARNING: THIS IS A TEST SYSTEM! YOU MAY NOT STORE ANY PRODUCTION DATA! </span></tpl>', "<tpl if=\"user !== '' \">", '<tpl if="values.isGuest">', '<form id="openid_login" class="x-login">', "<div>", '<div style="display:inline;" class="login_name">{user}</div>', '<input type="submit" class="button" value="{Signavio.I18N.Repository.Header.login}" style="{[!Signavio.Config.SHOW_LOGIN_LOGOUT ? "display:none" : ""]}"/>', "</div>", "</form>", "</tpl>", '<tpl if="!values.isGuest">', '<form action="' + Signavio.Config.LOGOUT_HANDLER + '" method="post" id="openid_login">', "<div>", '<div style="display:inline;" class="login_name">{user}</div>', '<input type="submit" class="button" value="{Signavio.I18N.Repository.Header.logout}" style="{[!Signavio.Config.SHOW_LOGIN_LOGOUT ? "display:none" : ""]}" />', "</div>", "</form>", "</tpl>", "</tpl>", '<div style="clear: both;"></div>', '</div><div class="signavio_repository_header_shadow"><div></div></div>');
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Templates) {
    Signavio.Templates = {}
}
Signavio.Templates.View = new Ext.XTemplate('<tpl if="!this.isSearchResult(values)" >', '<tpl for=".">', '<div class="thumb-wrap" id="{href}" storeindex="{#}" unselectable="on">', '<tpl for="rep">', '<div class="thumb {[parent.rel == Signavio.Const.REL.MODEL ? "model"  : ""]}"><img src="{picture}" title="{name}" class="x-thumb-icon"></div>', '<div class="x-badge">', '<tpl if="values.granted_revision && values.granted_revision == values.revision">', '<img src="/explorer/src/img/nuvola/16x16/apps/browser.png" ext:qtip="{[Signavio.I18N.Repository.Revision.viewIconTooltipHead]}">', "</tpl>", '<tpl if="values.granted_revision && values.granted_revision != values.revision">', '<img src="/explorer/src/img/nuvola/16x16/apps/browser_black.png" ext:qtip="{[Signavio.I18N.Repository.Revision.viewIconTooltipNoHead]}">', "</tpl>", "</div>", '<span class="x-editable" title="{[values.name != values.shortName ? values.name : "" ]}">{[values.shortName||"&nbsp;"]}</span>', "</tpl>", "</div>", "</tpl>", '<div class="x-clear"></div>', "</tpl>", '<tpl if="this.isSearchResult(values)" >', '<tpl if="this.hasSearchResultsFoundInName(values)" >', '<div class="x-grid-group dataview">', '<div class="x-grid-group-hd dataview-group-header" style="border-bottom:2px solid #DA86AE;margin-right: 6px;">', '<div style="color:#AD0F5B;">{[Signavio.I18N.Repository.View.foundInNames]}</div>', "</div>", '<div class="x-grid-group-body">', '<tpl for=".">', '<tpl if="this.isSearchResultFoundInName(values)" >', '<div class="thumb-wrap" id="{href}" storeindex="{#}">', '<tpl for="rep">', '<div class="thumb {[parent.rel == Signavio.Const.REL.MODEL ? "model"  : ""]}"><img src="{picture}" title="{name}" class="x-thumb-icon"></div>', '<span class="x-editable" title="{[values.name != values.shortName ? values.name : "" ]}">{[values.shortName||"&nbsp;"]}</span>', "</tpl>", "</div>", "</tpl>", "</tpl>", "</div>", "</div>", '<div class="x-clear"></div>', "</tpl>", '<tpl if="this.hasSearchResultsFoundInDescription(values)" >', '<div class="x-grid-group dataview">', '<div class="x-grid-group-hd dataview-group-header" style="border-bottom:2px solid #DA86AE;margin-right: 6px;">', '<div style="color:#AD0F5B;">{[Signavio.I18N.Repository.View.foundInDescriptions]}</div>', "</div>", '<div class="x-grid-group-body">', '<tpl for=".">', '<tpl if="this.isSearchResultFoundInDescription(values)" >', '<div class="thumb-wrap" id="{href}" storeindex="{#}">', '<tpl for="rep">', '<div class="thumb {[parent.rel == Signavio.Const.REL.MODEL ? "model"  : ""]}"><img src="{picture}" title="{name}" class="x-thumb-icon"></div>', '<span class="x-editable" title="{[values.name != values.shortName ? values.name : "" ]}">{[values.shortName||"&nbsp;"]}</span>', "</tpl>", "</div>", "</tpl>", "</tpl>", "</div>", "</div>", '<div class="x-clear"></div>', "</tpl>", '<tpl if="this.hasSearchResultsFoundInRevComment(values)" >', '<div class="x-grid-group dataview">', '<div class="x-grid-group-hd dataview-group-header" style="border-bottom:2px solid #DA86AE;margin-right: 6px;">', '<div style="color:#AD0F5B;">{[Signavio.I18N.Repository.View.foundInRevComments]}</div>', "</div>", '<div class="x-grid-group-body">', '<tpl for=".">', '<tpl if="this.isSearchResultFoundInRevComment(values)" >', '<div class="thumb-wrap" id="{href}" storeindex="{#}">', '<tpl for="rep">', '<div class="thumb {[parent.rel == Signavio.Const.REL.MODEL ? "model"  : ""]}"><img src="{picture}" title="{name}" class="x-thumb-icon"></div>', '<span class="x-editable" title="{[values.name != values.shortName ? values.name : "" ]}">{[values.shortName||"&nbsp;"]}</span>', "</tpl>", "</div>", "</tpl>", "</tpl>", "</div>", "</div>", '<div class="x-clear"></div>', "</tpl>", '<tpl if="this.hasSearchResultsFoundInLabels(values) || this.hasSearchResultsFoundInMetaData(values)" >', '<div class="x-grid-group dataview">', '<div class="x-grid-group-hd dataview-group-header" style="border-bottom:2px solid #DA86AE;margin-right: 6px;">', '<div style="color:#AD0F5B;">{[Signavio.I18N.Repository.View.foundInLabels]}</div>', "</div>", '<div class="x-grid-group-body">', '<tpl for=".">', '<tpl if="this.isSearchResultFoundInLabels(values) || this.isSearchResultFoundInMetaData(values)" >', '<div class="thumb-wrap" id="{href}" storeindex="{#}">', '<tpl for="rep">', '<div class="thumb {[parent.rel == Signavio.Const.REL.MODEL ? "model"  : ""]}"><img src="{picture}" title="{name}" class="x-thumb-icon"></div>', '<span class="x-editable" title="{[values.name != values.shortName ? values.name : "" ]}">{[values.shortName||"&nbsp;"]}</span>', "</tpl>", "</div>", "</tpl>", "</tpl>", "</div>", "</div>", '<div class="x-clear"></div>', "</tpl>", '<tpl if="this.hasSearchResultsFoundInComment(values)" >', '<div class="x-grid-group dataview">', '<div class="x-grid-group-hd dataview-group-header" style="border-bottom:2px solid #DA86AE;margin-right: 6px;">', '<div style="color:#AD0F5B;">{[Signavio.I18N.Repository.View.foundInComments]}</div>', "</div>", '<div class="x-grid-group-body">', '<tpl for=".">', '<tpl if="this.isSearchResultFoundInComment(values)" >', '<div class="thumb-wrap" id="{href}" storeindex="{#}">', '<tpl for="rep">', '<div class="thumb {[parent.rel == Signavio.Const.REL.MODEL ? "model"  : ""]}"><img src="{picture}" title="{name}" class="x-thumb-icon"></div>', '<span class="x-editable" title="{[values.name != values.shortName ? values.name : "" ]}">{[values.shortName||"&nbsp;"]}</span>', "</tpl>", "</div>", "</tpl>", "</tpl>", "</div>", "</div>", '<div class="x-clear"></div>', "</tpl>", "</tpl>", {isSearchResult: function (a) {
    return a[0] && a[0].rep && a[0].rep.fields
}, hasSearchResultsFoundInName: function (b) {
    for (var a = 0; a < b.length; a++) {
        if (this.isSearchResultFoundInName(b[a])) {
            return true
        }
    }
    return false
}, isSearchResultFoundInName: function (a) {
    return a.rep && a.rep.fields && (a.rep.fields.indexOf("name") != -1)
}, hasSearchResultsFoundInDescription: function (b) {
    for (var a = 0; a < b.length; a++) {
        if (this.isSearchResultFoundInDescription(b[a])) {
            return true
        }
    }
    return false
}, isSearchResultFoundInDescription: function (a) {
    return a.rep && a.rep.fields && (a.rep.fields.indexOf("description") != -1) && !this.isSearchResultFoundInName(a)
}, hasSearchResultsFoundInRevComment: function (b) {
    for (var a = 0; a < b.length; a++) {
        if (this.isSearchResultFoundInRevComment(b[a])) {
            return true
        }
    }
    return false
}, isSearchResultFoundInRevComment: function (a) {
    return a.rep && a.rep.fields && (a.rep.fields.indexOf("text1") != -1) && !this.isSearchResultFoundInDescription(a) && !this.isSearchResultFoundInName(a)
}, hasSearchResultsFoundInLabels: function (b) {
    for (var a = 0; a < b.length; a++) {
        if (this.isSearchResultFoundInLabels(b[a])) {
            return true
        }
    }
    return false
}, isSearchResultFoundInLabels: function (a) {
    return a.rep && a.rep.fields && (a.rep.fields.indexOf("labels") != -1) && !this.isSearchResultFoundInRevComment(a) && !this.isSearchResultFoundInDescription(a) && !this.isSearchResultFoundInName(a)
}, hasSearchResultsFoundInMetaData: function (b) {
    for (var a = 0; a < b.length; a++) {
        if (this.isSearchResultFoundInMetaData(b[a])) {
            return true
        }
    }
    return false
}, isSearchResultFoundInMetaData: function (a) {
    return a.rep && a.rep.fields && (a.rep.fields.indexOf("text2") != -1) && !this.isSearchResultFoundInRevComment(a) && !this.isSearchResultFoundInDescription(a) && !this.isSearchResultFoundInName(a)
}, hasSearchResultsFoundInComment: function (b) {
    for (var a = 0; a < b.length; a++) {
        if (this.isSearchResultFoundInComment(b[a])) {
            return true
        }
    }
    return false
}, isSearchResultFoundInComment: function (a) {
    return a.rep && a.rep.fields && (a.rep.fields.indexOf("comments") != -1) && !this.isSearchResultFoundInLabels(a) && !this.isSearchResultFoundInMetaData(a) && !this.isSearchResultFoundInRevComment(a) && !this.isSearchResultFoundInDescription(a) && !this.isSearchResultFoundInName(a)
}});
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Templates) {
    Signavio.Templates = {}
}
Signavio.Templates.InfoModel = new Ext.XTemplate('<div class="details">', '<tpl for=".">', '<span id="info_name" class="x-info-name {[values.notEditable ? "" : "x-editable"]} {[!values.name?"x-default":""]}">{[values.name||Signavio.I18N.Repository.Info.Attributes.noname]}</span>', '<div class="x-info-updated">{info}</div>', '<div class="x-info-notify">{[values.showNotify ? values.notify ? Signavio.I18N.Repository.Info.willBeNotified : Signavio.I18N.Repository.Info.wontBeNotified : ""]}<a class="x-info-notify-link" href="#">{[values.showNotify ? values.notify ? Signavio.I18N.Repository.Info.dontNotifyMe : Signavio.I18N.Repository.Info.notifyMe : ""]}</a></div>', '<div id="info_description" class="x-info-description {[values.notEditable ? "" : "x-editable"]} {[!values.description?"x-default":""]}">{[values.description.gsub("\n", "<br/>")||Signavio.I18N.Repository.Info.Attributes.nodescription]}</div>', "</tpl>", "</div>");
Signavio.Templates.InfoFolder = new Ext.XTemplate('<div class="details">', '<tpl for=".">', '<span id="info_name" class="x-info-name {[values.notEditableTitle||values.notEditable ? "" : "x-editable"]} {[!values.name?"x-default":""]}">{[values.name||Signavio.I18N.Repository.Info.Attributes.noname]}</span>', '<div id="info_description" class="x-info-description {[values.notEditable ? "" : "x-editable"]} {[!values.description?"x-default":""]}">{[values.description||Signavio.I18N.Repository.Info.Attributes.nodescription]}</div>', "</tpl>", "</div>");
Signavio.Templates.InfoMultiple = new Ext.XTemplate('<div class="details">', '<tpl for=".">', '<div class="x-info-count">{count} {[Signavio.I18N.Repository.Info.elementSelected]}</div>', '<div class="x-info-updated">{updated}</div>', "</tpl>", "</div>");
Signavio.Templates.InfoRootFolder = new Ext.XTemplate('<div class="details">', '<tpl for=".">', '<span id="info_name" class="x-info-name">{name}</span>', "</tpl>", "</div>");
Signavio.Templates.No = new Ext.XTemplate('<div class="details">', '<span class="x-no-selection">' + Signavio.I18N.Repository.Info.noSelection + "</span>", "</div>");
Signavio.Templates.InfoModel.compile();
Signavio.Templates.InfoFolder.compile();
Signavio.Templates.InfoMultiple.compile();
Signavio.Templates.InfoRootFolder.compile();
Signavio.Templates.No.compile();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Templates) {
    Signavio.Templates = {}
}
Signavio.Templates.Revision = new Ext.XTemplate('<tpl for=".">', '<tpl if="parent.length &gt; 1">', '<div class="x-revision-model {[xindex === 1 ? "x-revision-mode-first" : ""]}">» <span>{[values.info.get("rep").name]}</span></div>', "</tpl>", '<tpl for="rev">', '<div class="x-revision-item">', '<div class="x-revision-item-header">', '<tpl if="values.isGranted && values.hasShareAccess">', '<img src="/explorer/src/img/nuvola/16x16/apps/browser.png" ext:qtip="{[Signavio.I18N.Repository.Revision.iconTooltip]}"/>', "</tpl>", '<span class="revision">Revision', '<tpl if="values.hasAccess">', "</span> ", '<a href="{editorLink}" target="_blank" class="revision">', "{rev}", "</a> ", "</tpl>", '<tpl if="!values.hasAccess">', " {rev}", "</span> ", "</tpl>", '<span class="date">{info}</span>', "</div>", '<div class="x-full-view-revision" style="display:none;">', '<div class="{[!values.comment?"":"comment"]}">', '{[(values.comment||"").gsub("\n", "<br/>")]}', "</div>", '<div class="x-revert">', '<tpl if="xindex != 1 && values.hasAccess">', '<a href="#" class="x-link-revert">{[Signavio.I18N.Repository.Revision.revert]}</a>', "</tpl>", '<tpl if="values.hasShareAccess && !values.isGranted">', '<a href="#" class="x-link-approve">{[Signavio.I18N.Repository.Revision.approve]}</a>', "</tpl>", '<tpl if="values.hasShareAccess && values.isGranted">', '<a href="#" class="x-link-approve x-link-unapprove">{[Signavio.I18N.Repository.Revision.unapprove]}</a>', "</tpl>", "</div>", "</div>", "</div>", "</tpl>", "</tpl>");
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Core) {
    Signavio.Core = {}
}
if (!Signavio.Core.Stores) {
    Signavio.Core.Stores = {}
}
new function () {
    Signavio.Core.Stores.RequestManager = {};
    var a = {};
    Signavio.Core.Stores.RequestManager.LOADED = "loaded";
    Signavio.Core.Stores.RequestManager.LOADED_FAILED = "loaded_failed";
    Signavio.Core.Stores.RequestManager.BEFORELOAD = "beforeload";
    Signavio.Core.Stores.RequestManager.FINISHED = "finished";
    Signavio.Core.Stores.RequestManager.on = function (d, b) {
        if (!a[d]) {
            a[d] = []
        }
        a[d].push(b)
    };
    Signavio.Core.Stores.RequestManager.un = function (d, b) {
        if (a[d] && a[d].include(b)) {
            a[d] = a[d].without(b)
        }
    };
    Signavio.Core.Stores.RequestManager.raise = function (d) {
        if (!a[d]) {
            return
        }
        var b = arguments;
        return a[d].all(function (e) {
            return e.apply(e, b) !== false
        })
    };
    Signavio.Core.Stores.RequestManager.onNextLoading = function (j, f) {
        var d = 0, h, g = true;
        var e = function () {
            ++d
        };
        var b = function (l, k) {
            --d;
            g = g && k;
            window.clearTimeout(h);
            h = window.setTimeout(function () {
                if (d === 0) {
                    Signavio.Core.Stores.RequestManager.un(Signavio.Core.Stores.RequestManager.BEFORELOAD, e);
                    Signavio.Core.Stores.RequestManager.un(Signavio.Core.Stores.RequestManager.FINISHED, b);
                    j(g)
                }
            }, f || 100)
        };
        Signavio.Core.Stores.RequestManager.on(Signavio.Core.Stores.RequestManager.BEFORELOAD, e);
        Signavio.Core.Stores.RequestManager.on(Signavio.Core.Stores.RequestManager.FINISHED, b)
    };
    Signavio.Core.Stores.RequestManager.doPost = function (d, b, e) {
        return Signavio.Core.Stores.RequestManager.doRequest(d, b, e, "post")
    };
    Signavio.Core.Stores.RequestManager.doGet = function (d, b, e) {
        return Signavio.Core.Stores.RequestManager.doRequest(d, b, e, "get")
    };
    Signavio.Core.Stores.RequestManager.doPut = function (d, b, e) {
        return Signavio.Core.Stores.RequestManager.doRequest(d, b, e, "put")
    };
    Signavio.Core.Stores.RequestManager.doDelete = function (d, b, e) {
        return Signavio.Core.Stores.RequestManager.doRequest(d, b, e, "delete")
    };
    Signavio.Core.Stores.RequestManager.doRequest = function (d, l, g, b, e) {
        var h = Signavio.Core.Stores.RequestManager.raise(Signavio.Core.Stores.RequestManager.BEFORELOAD);
        if (h === false) {
            return
        }
        var j = l instanceof Array && l.length > 0 ? l[0] : l;
        var f = l instanceof Array && l.length > 1 ? l[1] : null;
        b = b && ["post", "delete", "put", "get"].include(b.toLowerCase()) ? b : "get";
        var k = (g || {}).timeout || 30000;
        if (g && g.timeout) {
            delete g.timeout
        }
        g = g || {};
        $H(g).keys().each(function (m) {
            g[m] = typeof g[m] == "object" ? (g[m] instanceof Array ? g[m].map(function (n) {
                return typeof n == "object" ? Object.toJSON(n) : n
            }) : Object.toJSON(g[m])) : g[m]
        });
        Ext.Ajax.request({url: d, timeout: k, params: g, method: b.toUpperCase(), disableCaching: true, headers: {Accept: "application/json", "Content-Type": "charset=UTF-8"}, success: function (m) {
            info("Success request to " + d);
            if (j instanceof Function) {
                var n = m;
                try {
                    n = !e ? m.responseText.evalJSON() || m : m
                } catch (m) {
                }
                j(n)
            }
            Signavio.Core.Stores.RequestManager.raise(Signavio.Core.Stores.RequestManager.LOADED, m);
            Signavio.Core.Stores.RequestManager.raise(Signavio.Core.Stores.RequestManager.FINISHED, m, true)
        }, failure: function (m) {
            warn(m.responseText && m.responseText.isJSON() ? m.responseText.evalJSON() : "On " + d + " status " + m.status + " - " + m.statusText);
            if (f instanceof Function) {
                f(!e && m.responseText && m.responseText.isJSON() ? m.responseText.evalJSON() : m)
            }
            Signavio.Core.Stores.RequestManager.raise(Signavio.Core.Stores.RequestManager.LOADED_FAILED, m);
            Signavio.Core.Stores.RequestManager.raise(Signavio.Core.Stores.RequestManager.FINISHED, m, false)
        }})
    }
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Core) {
    Signavio.Core = {}
}
new function () {
    var a = $H({});
    Signavio.Core.StoreManager = {};
    Signavio.Core.StoreManager.getRootDirectoryStore = function (b) {
        return Signavio.Core.StoreManager.getStore("root") || Signavio.Core.StoreManager.getRelatedStore(Signavio.Config.DIRECTORY_PATH, "root", ["dir", "glos"], {callback: b})
    };
    Signavio.Core.StoreManager.loadChildEntitiesStore = function (b) {
        Signavio.Core.StoreManager.getRelatedStore(b, "child", ["mod", "dir"], {sort: Signavio.Helper.BusinessObjectSortingFunction})
    };
    Signavio.Core.StoreManager.setChildEntitiesStore = function (b) {
        var e = a.get("child");
        if (e) {
            var d = e.lastOptions || {};
            d.filter = ["mod", "dir"];
            d.sort = Signavio.Helper.BusinessObjectSortingFunction;
            e.reloadWith(b, d)
        }
    };
    Signavio.Core.StoreManager.reloadChildEntitiesStore = function () {
        var d = a.get("child");
        if (d) {
            var b = d.lastOptions || {};
            b.filter = ["mod", "dir"];
            b.sort = Signavio.Helper.BusinessObjectSortingFunction;
            b.force = true;
            delete b.callee;
            b.callback = function () {
                delete d.lastOptions.callback;
                delete d.lastOptions.force
            };
            d.load(b)
        }
    };
    Signavio.Core.StoreManager.getChildEntitiesStore = function () {
        var b = Signavio.Core.StoreManager.getRelatedStore(null, "child");
        return b
    };
    Signavio.Core.StoreManager.loadCurrentUser = function (b) {
        Signavio.Core.StoreManager.getRelatedStore(b, "cuser")
    };
    Signavio.Core.StoreManager.getCurrentUser = function () {
        return Signavio.Core.StoreManager.getRelatedStore(null, "cuser")
    };
    Signavio.Core.StoreManager.getAllUserStore = function (b) {
        return Signavio.Core.StoreManager.getRelatedStore(Signavio.Config.USER_PATH, undefined, undefined, b instanceof Function ? {callback: b} : undefined)
    };
    Signavio.Core.StoreManager.getAllUserGroupStore = function () {
        var d = Signavio.Core.StoreManager.getStore("userandgroups");
        if (d) {
            return d
        }
        d = Signavio.Core.StoreManager.getRelatedStore(undefined, "userandgroups");
        var b = Signavio.Core.StoreManager.getAllUserStore(function () {
            d.reloadWith(b, {add: true})
        });
        var e = Signavio.Core.StoreManager.getAllGroupStore(function () {
            d.reloadWith(e, {add: true})
        });
        return d
    };
    Signavio.Core.StoreManager.getAllGroupStore = function (b) {
        return Signavio.Core.StoreManager.getRelatedStore(Signavio.Config.USERGROUP_PATH, "usergroups", undefined, b instanceof Function ? {callback: b} : undefined)
    };
    Signavio.Core.StoreManager.getStore = function (b, d, e) {
        if (d === true) {
            return Signavio.Core.StoreManager.getRelatedStore(b, undefined, undefined, {callback: e})
        } else {
            return a.get(b)
        }
    };
    Signavio.Core.StoreManager.deleteStore = function (b) {
        info("Delete store " + b.getIdentifier() + " from cache");
        a.unset(b.getIdentifier());
        delete b
    };
    Signavio.Core.StoreManager.clearAllStores = function () {
        a.values().invoke("clearCache")
    };
    Signavio.Core.StoreManager.getAllStores = function () {
        return a.values()
    };
    Signavio.Core.StoreManager.reload = function (b, e) {
        var d = [];
        (b === undefined ? a.values().findAll(function (f) {
            return !(d.include(f.getIdentifier()) || !d.push(f.getIdentifier()))
        }) : [b]).each(function (f) {
            var g = Object.clone(f.lastOptions);
            delete g.add;
            delete g.callee;
            g.force = true;
            g.callback = function () {
                delete f.lastOptions.callback;
                delete f.lastOptions.force;
                if (e instanceof Function) {
                    e()
                }
            };
            if (g.url) {
                f.load(g)
            }
        })
    };
    Signavio.Core.StoreManager.getRelatedStore = function (h, e, g, f) {
        var d = a.get(e) || a.get(h);
        var b = !!d;
        if (!d) {
            if ((h || "").startsWith(Signavio.Config.USERGROUP_PATH)) {
                d = new Signavio.Core.UserGroupStore({url: ""})
            } else {
                if ((h || "").startsWith(Signavio.Config.TENANT_PATH)) {
                    d = new Signavio.Core.UserGroupStore({url: ""})
                } else {
                    if ((h || "").startsWith(Signavio.Config.MODEL_PATH)) {
                        d = new Signavio.Core.ModelStore({url: ""})
                    } else {
                        d = new Signavio.Core.DataStore({url: ""})
                    }
                }
            }
            if (h && !a.get(h)) {
                a.set(h, d)
            }
            if (e && !a.get(e)) {
                a.set(e, d)
            }
        }
        g = g || [];
        f = f ? Object.clone(f) : d.lastOptions || {};
        f.key = e;
        if (h) {
            f.id = h;
            f.filter = g;
            f.url = Signavio.Config.BACKEND_PATH + h;
            d.load(f)
        }
        return d
    }
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Core) {
    Signavio.Core = {}
}
new function () {
    Signavio.Core.RecordCreator = {};
    Signavio.Core.RecordCreator.create = function (f, h, k) {
        var j = Ext.data.Record.create(["rel", "href", "rep"]);
        var g = new j({rel: f, href: h, rep: k});
        return g
    };
    var d = Signavio.Core.Stores.RequestManager;
    var e = {};
    e.PATTERN = new RegExp("(" + Signavio.Config.BACKEND_PATH + "/p/)?([^/]+)(/([^/]+))?(/([^/]+))?(/+(.*))?$");
    e.split = function (g) {
        if (!g || !(g.startsWith("/"))) {
            throw"URLSpliter.split needs a relative url starting with a /."
        }
        var f = g.match(e.PATTERN);
        o = {};
        o.context = f[2];
        o.identifier = f[4];
        o.extension = f[6];
        o.param = f[8];
        return o
    };
    var a = {};
    a.setRecord = function (j, h, g) {
        var f = Signavio.Core.StoreManager.getStore(j);
        if (!f) {
            return
        }
        f.each(function (k) {
            if (k.get("href") === h) {
                k.set("rep", g instanceof Array ? g.clone() : Object.clone(g), f.indexOf(k) < 0);
                k.commit(true)
            }
        });
        a.cacheStore(f)
    };
    a.cacheStore = function (f) {
        if (!f || !f.lastOptions || !f.lastOptions.url) {
            return
        }
        a.setCache(f.lastOptions.url, (f.snapshot || f.data).items)
    };
    a.setCache = function (h, g) {
        var f = b.get(h);
        if (f) {
            f.records = g;
            f.totalRecords = f.records.length;
            f.success = true
        }
    };
    a.updateAllStores = function (g, f) {
        Signavio.Core.StoreManager.getAllStores().each(function (h) {
            h.each(function (j) {
                if (j.get("href") === g) {
                    j.set("rep", f, h.indexOf(j) < 0);
                    j.commit(true);
                    h.fireEvent("update", h, j, Ext.data.Record.COMMIT);
                    a.cacheStore(h)
                }
            })
        })
    };
    a.doUpdate = function (l, n) {
        info("Do update on record " + l.get("href"));
        var g = l.get("href");
        var f = Signavio.Config.BACKEND_PATH + g;
        var m = l.get("rep");
        var h = l.modified.rep;
        var k = e.split(g);
        var p = function (q) {
            m = q;
            l.set("rep", Object.clone(m));
            l.commit(true);
            if (n.lastOptions.key) {
            }
            if (k.extension == "info") {
                a.updateAllStores("/" + k.context + "/" + k.identifier, m);
                if (!n.lastOptions.key && n.lastOptions.id.startsWith("/user")) {
                    a.setRecord("cuser", "/" + k.context + "/" + k.identifier + "/" + k.extension, m)
                }
            }
            a.cacheStore(n);
            this.afterAsyncCommit(l);
            this.fireEvent("submitted", this, l)
        }.bind(n);
        var j = function () {
            if (h) {
                l.set("rep", h);
                l.commit(true)
            }
            this.fireEvent("submitfailed", this, l);
            this.afterAsyncCommit(l)
        }.bind(n);
        if ($H(m).values().any(function (q) {
            return q instanceof Array || "string" == typeof q
        })) {
            for (i in m) {
                if (i && m[i] && "string" == typeof m[i]) {
                    m[i] = Signavio.Utils.unescapeHTML(m[i]);
                    continue
                } else {
                    if (!i || !(m[i] instanceof Array)) {
                        continue
                    }
                }
                m[i] = m[i].map(function (q) {
                    return typeof q === "string" ? q : Object.toJSON(q)
                })
            }
        }
        d.doRequest(f, [p, j], m, "put")
    };
    a.doDeletes = function (h, m, j) {
        info("Deletes internal representation on record " + h.get("href"));
        var g = h.modified.rep;
        var l = h.get("rep");
        var n = function () {
            this.afterAsyncCommit(h);
            this.fireEvent("submitted", this, h)
        }.bind(j);
        var f = function () {
            l.splice(g.indexOf(m), 0, m);
            h.set("rep", l);
            h.commit(true);
            this.fireEvent("submitfailed", this, h);
            this.afterAsyncCommit(h)
        }.bind(j);
        var k = Signavio.Config.BACKEND_PATH + m.href;
        d.doRequest(k, [n, f], null, "delete")
    };
    a.doAppend = function (h, l, j) {
        info("Append internal representation on record " + h.get("href"));
        var k = h.get("href");
        var f = e.split(k);
        var m = function (n) {
            h.get("rep").each(function (p) {
                if (p.href == l.href) {
                    p.rep = n.rep || n;
                    p.rel = n.rel || h.get("rel");
                    p.href = n.href || l.href || h.get("href")
                }
            });
            this.fireEvent("submitted", this, h);
            this.afterAsyncCommit(h);
            a.setRecord("/" + f.context + "/" + f.identifier, k, h.get("rep"))
        }.bind(j);
        var g = function () {
            delete h.get("rep")[h.get("rep").indexOf(n)];
            var n = h.get("rep").compact();
            h.set("rep", n);
            h.commit(true);
            this.fireEvent("submitfailed", this, h);
            this.afterAsyncCommit(h)
        }.bind(j);
        d.doRequest(Signavio.Config.BACKEND_PATH + k, [m.bind(m), g.bind(g)], l.rep || l, "post")
    };
    a.doAdd = function (h, j) {
        info("Add a new record " + h.get("href") + " to the store " + j.lastOptions.id);
        var m = j.lastOptions.id;
        var f = e.split(m);
        var k = Signavio.Config.BACKEND_PATH + "/" + f.context + (f.extension ? "/" + f.identifier + "/" + f.extension : "");
        var l = function (n) {
            h.set("rel", n.rel, true);
            h.set("href", n.href, true);
            h.set("rep", n.rep, true);
            h.commit(true);
            a.cacheStore(j);
            var p = Signavio.Core.StoreManager.getStore(j.getIdentifier());
            if (p && j !== p && p.indexOf(h) < 0) {
            }
            if (j.lastOptions && j.lastOptions.key === "child") {
                var p = Signavio.Core.StoreManager.getStore(j.getIdentifier());
                if (p && p !== this && p.lastOptions.id === this.lastOptions.id) {
                    p.add([h])
                }
            }
            this.fireEvent("submitted", this, h);
            this.fireEvent("add", this, [h], j.indexOf(h))
        }.bind(j);
        var g = function (n) {
            this.fireEvent("submitfailed", this, h);
            j.remove(h);
            a.cacheStore(j)
        }.bind(j);
        d.doRequest(k, [l, g], Ext.apply((Object.clone(h.get("rep")) || {}), {parent: m}), "post")
    };
    a.doChildUpdate = function (l, m, q) {
        info("Do an update on the child " + (m.href || m) + " within the record " + l.get("href"));
        var g = l.get("href");
        var h = e.split(g);
        var n = l.get("rep");
        var j = l.modified.rep;
        var r = function (s) {
            l.get("rep").each(function (t) {
                if (t.href == m.href) {
                    t.rep = s.rep || s;
                    t.rel = s.rel || m.rel || l.get("rel")
                }
            });
            this.fireEvent("submitted", this, l);
            this.afterAsyncCommit(l);
            a.setRecord("/" + h.context + "/" + h.identifier, g, l.get("rep"))
        }.bind(q);
        var k = function (s) {
            n[n.indexOf(m)].rep = j.indexOf(m).rep;
            l.set("rep", n);
            l.commit(true);
            this.fireEvent("submitfailed", this, l);
            this.afterAsyncCommit(l)
        }.bind(q);
        var f = Signavio.Config.BACKEND_PATH + m.href;
        var p = m.rep || m;
        d.doRequest(f, [r.bind(r), k.bind(k)], p, "put")
    };
    a.doMove = function (h, n, j) {
        info("Move the record " + h.get("href") + " from " + j.lastOptions.id + " to " + n);
        var m = h.get("href");
        var k = Signavio.Config.BACKEND_PATH + m;
        var f = e.split(m);
        var l = function (t) {
            var q = h.copy();
            j.remove(h);
            var v = Signavio.Core.StoreManager.getStore(j.getIdentifier());
            if (v && j !== v) {
                var u = v.data.items.find(function (w) {
                    return w.get("href") === h.get("href")
                });
                if (u) {
                    v.remove(u)
                }
            }
            var p = b.get(Signavio.Config.BACKEND_PATH + n);
            if (p) {
                if (p.records instanceof Array) {
                    p.records.push(q)
                } else {
                    p.records = [q]
                }
                p.totalRecords = p.records.length
            }
            var r = Signavio.Core.StoreManager.getStore(n);
            if (r) {
                r.add([q])
            }
            var s = Signavio.Core.StoreManager.getStore(m);
            if (s) {
                s.loadData(t)
            }
        }.bind(j);
        var g = function (p) {
            this.fireEvent("submitfailed", this, h);
            this.afterAsyncCommit(h)
        }.bind(j);
        d.doRequest(k, [l, g], {parent: n}, "put")
    };
    a.doCopy = function (g, l, h) {
        info("Copy the record " + g.get("href") + " to the store " + l);
        var j = Signavio.Config.BACKEND_PATH + Signavio.Config.MODEL_PATH;
        var k = function (n) {
            var p = Signavio.Core.StoreManager.getStore("child");
            if (p && p.lastOptions.id === l) {
                var m = Signavio.Core.RecordCreator.create(n.rel, n.href, n.rep);
                p.add([m])
            }
        }.bind(h);
        var f = function (m) {
            this.fireEvent("submitfailed", this, g)
        }.bind(h);
        d.doRequest(j, [k, f], {parent: l, id: g.get("href"), name: g.get("rep").name, copy: true}, "post")
    };
    a.doSetGroupParents = function (n, j, k) {
        info("Set the parents from record " + n.get("href") + " to " + j.join(","));
        var m = n.get("href");
        var h = Signavio.Config.BACKEND_PATH + m;
        var f = e.split(m);
        var l = function (p) {
            var t = Signavio.Core.StoreManager.getStore(m);
            if (t) {
                t.loadData(p);
                t.addedRecords = [];
                t.removedRecords = [];
                a.cacheStore(t);
                t.fireEvent("update", t, n, Ext.data.Record.COMMIT)
            }
            if (j.include(k.getIdentifier())) {
                var s = p.find(function (u) {
                    return u.rel == "info"
                });
                k.add(Signavio.Core.RecordCreator.create(n.get("rel") === "user" ? "user" : "cgroup", m, Object.clone(s.rep)));
                k.addedRecords = []
            } else {
                var q = k.findByIdentifier(n.get("href"));
                if (q) {
                    k.remove(q);
                    k.removedRecords = []
                }
            }
            a.cacheStore(k);
            this.fireEvent("submitted", this, n);
            this.afterAsyncCommit(n)
        }.bind(k);
        var g = function (p) {
            this.fireEvent("submitfailed", this, n);
            this.afterAsyncCommit(n)
        }.bind(k);
        d.doRequest(h, [l, g], {parents: j}, "put")
    };
    a.doRemove = function (h, j) {
        info("Removes a record " + h.get("href") + " from the store " + j.lastOptions.id);
        var n = h.get("href");
        var l = Signavio.Config.BACKEND_PATH + n;
        var f = e.split(n);
        var k = j.indexOf(h);
        var m = function (p) {
            var q = Signavio.Core.StoreManager.getStore(n);
            if (q) {
                Signavio.Core.StoreManager.deleteStore(q)
            }
            this.fireEvent("submitted", this, h);
            a.cacheStore(j)
        }.bind(j);
        var g = function (p) {
            j.insert(Math.max(k, 0), [h]);
            this.fireEvent("submitfailed", this, h);
            this.fireEvent("update", this, h, Ext.data.Record.COMMIT)
        }.bind(j);
        d.doRequest(l, [m, g], null, "delete")
    };
    var b = $H({});
    b.getRecord = function (f, h) {
        var g;
        b.values().find(function (j) {
            g = j.records.find(function (k) {
                return k.get("rel") === f && k.get("href") === h
            });
            return g
        });
        return g
    };
    b.stores = $H({});
    b.register = function (g, h, f) {
        this.set(g, h);
        if (!b.stores.get(g)) {
            b.stores.set(g, [])
        }
        if (!b.stores.get(g).include(f)) {
            b.stores.get(g).push(f)
        }
    };
    b.raise = function (f) {
        (b.stores.get(f) || []).each(function (g) {
            g.fireEvent("load", g, g.data.items, g.lastOptions)
        })
    };
    Signavio.Core.DataStore = function (f) {
        Signavio.Core.DataStore.superclass.constructor.call(this, Ext.apply(f, {proxy: new Ext.data.HttpProxy({useAjax: true, url: f.url, method: "GET", disableCaching: true, headers: {accept: "application/json"}}), reader: new Ext.data.JsonReader(f, ["rel", "href", "rep"])}));
        this.proxy.on("loadexception", function (h, g, k) {
            if (k.status === 404) {
                return
            }
            var j = k.responseText || k.statusText + " on " + g.url;
            warn(j.isJSON() ? j.evalJSON() : j)
        });
        this.on("add", this.onAdd.bind(this), this);
        this.on("remove", this.onRemove.bind(this), this);
        this.baseParams = {timeout: 120000}
    };
    Ext.extend(Signavio.Core.DataStore, Ext.data.Store, {getPrivileges: function () {
        var f = this.getRecords(Signavio.Const.REL.PRIVILEGE);
        if (!f || f.length <= 0) {
            return null
        }
        return f[0].get("rep").map(function (g) {
            return g && g.rep && g.rep.privilege ? g.rep.privilege.toLowerCase() : undefined
        }).compact()
    }, unfoldPrivileges: function (f) {
        if (!f) {
            return[]
        }
        if (!(f instanceof Array)) {
            f = [f]
        }
        f.each(function (g) {
            switch (g) {
                case Signavio.Config.RIGHTS.READ:
                    f.push(Signavio.Config.RIGHTS.WRITE);
                case Signavio.Config.RIGHTS.WRITE:
                    f.push(Signavio.Config.RIGHTS.MODIFY);
                case Signavio.Config.RIGHTS.MODIFY:
                    f.push(Signavio.Config.RIGHTS.DELETE);
                case Signavio.Config.RIGHTS.DELETE:
                    f.push(Signavio.Config.RIGHTS.ALL);
                    f.push(Signavio.Config.RIGHTS.SHARE);
                case Signavio.Config.RIGHTS.ALL:
                case Signavio.Config.RIGHTS.SHARE:
                    break
            }
        });
        return f.uniq()
    }, hasPrivileges: function (f) {
        if (!f) {
            return false
        }
        if (!(f instanceof Array)) {
            f = [f]
        } else {
            f = f.clone()
        }
        f = this.unfoldPrivileges(f);
        var g = this.getPrivileges();
        if (g === null) {
            return true
        } else {
            if (g.include(Signavio.Config.RIGHTS.ALL)) {
                return true
            } else {
                if (g.length <= 0) {
                    return false
                } else {
                    return f.any(function (h) {
                        return g.include(h.toLowerCase())
                    })
                }
            }
        }
    }, filter: function (h, f) {
        if (typeof h == "string" && h.indexOf("rep.") >= 0) {
            f = f.toLowerCase();
            var g = h.split("||").map(function (j) {
                return j.split(".").last()
            }).compact();
            this.snapshot = this.snapshot || this.data;
            this.data = this.snapshot.filterBy(function (k) {
                var l = k.get("rep");
                var j = $H(l).keys();
                if (g && g.length > 0) {
                    j = j.findAll(function (m) {
                        return g.include(m)
                    })
                }
                return j.any(function (m) {
                    return l[m].toLowerCase().indexOf(f) >= 0
                })
            });
            this.fireEvent("datachanged", this)
        } else {
            Signavio.Core.DataStore.superclass.filter.apply(this, arguments)
        }
    }, onAdd: function (k, g, j) {
        var h = function (n, p) {
            var m = Object.toJSON(p.get("rep"));
            return n.any(function (q) {
                return q.get("href") == p.get("href") && Object.toJSON(q.get("rep")) == m
            })
        };
        this.addedRecords = ((this.addedRecords || []).concat(g)).uniq();
        this.addedRecords = this.addedRecords.findAll(function (m) {
            return !h(this.removedRecords || [], m)
        }.bind(this));
        this.removedRecords = (this.removedRecords || []).findAll(function (m) {
            return !h(g, m)
        });
        a.cacheStore(this);
        var l = Signavio.Core.StoreManager.getStore("child");
        if (l && l !== this && l.getIdentifier() === this.getIdentifier()) {
        }
        var f = Signavio.Core.StoreManager.getStore("cuser");
        if (f && f !== this && f.getIdentifier() === this.getIdentifier()) {
            f.add(g)
        }
    }, onRemove: function (j, k, h) {
        var g = function (n, p) {
            var m = Object.toJSON(p.get("rep"));
            return n.any(function (q) {
                return q.get("href") == p.get("href") && Object.toJSON(q.get("rep")) == m
            })
        };
        this.removedRecords = ((this.removedRecords || []).concat(!g(this.addedRecords || [], k) ? [k] : null)).compact().uniq();
        this.addedRecords = (this.addedRecords || []).without(k);
        a.cacheStore(this);
        var l = Signavio.Core.StoreManager.getStore("child");
        if (l && l !== this && l.getIdentifier() === this.getIdentifier()) {
        }
        var f = Signavio.Core.StoreManager.getStore("cuser");
        if (f && f !== this && f.getIdentifier() === this.getIdentifier()) {
            f.remove(k)
        }
    }, include: function (f) {
        return this.indexOf(f) >= 0
    }, findByIdentifier: function (f) {
        return this.getAt(this.find("href", f))
    }, findByRepValue: function (f, g) {
        return this.getAt(this.findBy(function (h) {
            return h.get("rep")[f] == g
        }))
    }, getIdentifier: function () {
        return(this.lastOptions || {}).id || this.id || ""
    }, getRecords: function (f) {
        var g = [];
        f = f instanceof Array ? f : (f ? [f] : null);
        (this.snapshot || this.data).each(function (h) {
            if (!f || f.include(h.get("rel"))) {
                g.push(h)
            }
        });
        return g
    }, parseValue: function (f, g) {
        return Signavio.Utils.extractValue(f, g)
    }, move: function (f, g) {
        if (!(f instanceof Ext.data.Record) || !this.findByIdentifier(f.get("href"))) {
            warn("DataStore.move needs a contained record");
            return
        }
        if (typeof g !== "string") {
            warn("DataStore.move needs a new parent identifier");
            return
        }
        a.doMove(f, g, this)
    }, copy: function (f, g) {
        if (!(f instanceof Ext.data.Record) || !this.data.items.pluck("data").pluck("href").include(f.get("href"))) {
            warn("DataStore.copy needs a contained record");
            return
        }
        if (typeof g !== "string") {
            warn("DataStore.copy needs a new parent identifier");
            return
        }
        a.doCopy(f, g, this)
    }, remove: function (f, g) {
        if (g === true) {
            this.fireEvent("beforesubmit", this, f);
            a.doRemove(f, this);
            Signavio.Core.DataStore.superclass.remove.call(this, f);
            this.removedRecords = (this.removedRecords || []).without(f)
        } else {
            Signavio.Core.DataStore.superclass.remove.call(this, f)
        }
    }, add: function (g, k) {
        if (k === true) {
            ([g] || []).each(function (l) {
                this.fireEvent("beforesubmit", this, l);
                a.doAdd(l, this)
            }.bind(this))
        }
        g = [].concat(g);
        if (g.length < 1) {
            return
        }
        for (var j = 0, f = g.length; j < f; j++) {
            g[j].join(this)
        }
        var h = this.data.length;
        this.data.addAll(g);
        if (this.snapshot) {
            this.snapshot.addAll(g)
        }
        if (k !== true) {
            this.fireEvent("add", this, g, h)
        }
        if (k === false) {
            ([g] || []).each(function (l) {
                this.addedRecords = this.addedRecords.without(l)
            }.bind(this))
        }
    }, commitChanges: function (g, h) {
        if (h instanceof Function) {
            Signavio.Core.Stores.RequestManager.onNextLoading(h)
        }
        if (this.removedRecords && this.removedRecords.length > 0) {
            var f = this.removedRecords.length - 1;
            for (; f >= 0; f--) {
                this.remove(this.removedRecords[f], true)
            }
            this.addedRecords = [];
            this.removedRecords = [];
            this.fireEvent("load", this, this.data.items, this.lastOptions)
        }
        if (g === true) {
            a.cacheStore(this);
            this.fireEvent("load", this, this.data.items, this.lastOptions);
            return
        }
        Signavio.Core.DataStore.superclass.commitChanges.call(this)
    }, rejectChanges: function () {
        if (this.removedRecords && this.removedRecords.length > 0) {
            var f = this.removedRecords.length - 1;
            for (; f >= 0; f--) {
                this.add(this.removedRecords[f])
            }
            this.addedRecords = [];
            this.removedRecords = []
        }
        Signavio.Core.DataStore.superclass.rejectChanges.call(this);
        this.fireEvent("load", this, this.data.items, this.lastOptions)
    }, missedCallbacks: [], load: function (g) {
        if (this.isLoading) {
            info("Store " + this.getIdentifier() + " is currently loading. Callback will be called after loading.");
            if (g.callback instanceof Function) {
                this.missedCallbacks.push(g.callback)
            }
            this.storeOptions(g);
            return false
        } else {
            this.missedCallbacks = []
        }
        this.isLoading = true;
        delete this.cleared;
        var f = g.url;
        this.id = g.id || f;
        this.proxy.conn.url = f;
        if (!f) {
            error("Data store needs a url")
        }
        if (Signavio.Config.USE_CACHE && !g.force && b.get(f)) {
            if (this.fireEvent("beforeload", this, g) !== false) {
                info("Loading store via cache on url " + f);
                window.setTimeout(function () {
                    this.storeOptions(g);
                    this.loadRecords.apply(this, [b.get(f), g, true])
                }.bind(this), 10)
            }
        } else {
            Signavio.Core.DataStore.superclass.load.call(this, g)
        }
        return true
    }, loadRecords: function (n, g, m) {
        delete this.isLoading;
        if (n && n.success === true) {
            var l = Object.clone(n);
            l.records = l.records.map(function (p) {
                return p.copy()
            });
            b.register(g.url, l, this)
        }
        if (!n || m === false) {
            this.fireEvent("load", this, [], g);
            if (g.callback) {
                g.callback.call(g.scope || this, [], g, false)
            }
            this.missedCallbacks.each(function (p) {
                p.call(g.scope || this, [], g, false)
            }.bind(this));
            this.missedCallbacks = [];
            return
        }
        var k = n.records, j = n.totalRecords || k.length;
        if (!g || g.add !== true) {
            if (this.pruneModifiedRecords) {
                this.modified = []
            }
            for (var h = 0, f = k.length; h < f; h++) {
                k[h].join(this)
            }
            if (this.snapshot) {
                this.data = this.snapshot;
                delete this.snapshot
            }
            this.data.clear();
            this.data.addAll(k);
            this.totalLength = j
        } else {
            this.totalLength = Math.max(j, this.data.length + k.length);
            this.add(k)
        }
        if (g.sort && g.sort instanceof Function) {
            this.data.sort("ASC", g.sort)
        }
        this.applySort();
        if (g.filter && g.filter.length > 0) {
            this.filterBy(function (p) {
                return g.filter.include(p.get("rel"))
            })
        }
        this.fireEvent("datachanged", this);
        this.fireEvent("load", this, k, g);
        if (g.callback) {
            g.callback.call(g.scope || this, k, g, true)
        }
        this.missedCallbacks.each(function (p) {
            p.call(g.scope || this, k, g, true)
        }.bind(this));
        this.missedCallbacks = []
    }, reloadWith: function (f, g) {
        g = g || {};
        g.url = (f.lastOptions || {}).url;
        g.id = (f.lastOptions || {}).id;
        if (this.fireEvent("beforeload", this, g) !== false) {
            window.setTimeout(function () {
                var h = (f.snapshot || f.data).items;
                this.storeOptions(g);
                this.loadRecords({records: h, totalRecords: h.length, success: true}, g, true);
                this.addedRecords = []
            }.bind(this), 10)
        }
    }, beforeCommit: function (h) {
        if (!h || !h.dirty) {
            return
        }
        this.fireEvent("beforesubmit", this, h);
        info("Start to commit " + h.get("href"));
        var j = h.get("href");
        var m = h.get("rep");
        var g = h.modified.rep || [];
        var l = [], n = [], f = [], k;
        if (m instanceof Array) {
            n = g.findAll(function (p) {
                return !m.any(function (q) {
                    return p.href == q.href && p.rep === q.rep
                })
            });
            l = m.findAll(function (p) {
                return !g.any(function (q) {
                    return p.href == q.href && p.rep === q.rep
                })
            });
            f = m.findAll(function (p) {
                return g.any(function (q) {
                    return p.href == q.href && p.rep && q.rep && p.rep !== q.rep
                }) && !l.include(p) && !n.include(p)
            })
        } else {
            if (m instanceof Object) {
                k = m
            }
        }
        if (n.length > 0 || l.length > 0 || f.length > 0) {
            n.each(function (p) {
                a.doDeletes(h, p, this)
            }.bind(this));
            l.each(function (p) {
                a.doAppend(h, p, this)
            }.bind(this));
            f.each(function (p) {
                a.doChildUpdate(h, p, this)
            }.bind(this))
        } else {
            if (k instanceof Object) {
                a.doUpdate(h, this)
            } else {
                info("No element to update");
                return
            }
        }
    }, afterCommit: function (f) {
    }, afterAsyncCommit: function (f) {
        (this.modified || []).remove(f);
        if (this.indexOf(f) >= 0) {
            this.fireEvent("update", this, f, Ext.data.Record.COMMIT)
        } else {
            this.fireEvent("load", this, this.data.items, this.lastOptions)
        }
    }, sortData: function (h, j) {
        if (h instanceof Function) {
            j = j || "ASC";
            var g = function (k, f) {
                var m = h.call(h, k), l = h.call(h, f);
                return m > l ? 1 : (m < l ? -1 : 0)
            };
            this.data.sort(j, g);
            if (this.snapshot && this.snapshot != this.data) {
                this.snapshot.sort(j, g)
            }
        } else {
            Signavio.Core.DataStore.superclass.sortData.call(this, h, j)
        }
    }, sortBy: function (f) {
        this.sortData(f, "ASC");
        this.fireEvent("datachanged", this)
    }, clearCache: function () {
        if (this.lastOptions && this.lastOptions.url) {
            b.unset(this.lastOptions.url);
            this.cleared = true
        }
    }});
    Signavio.Core.ModelStore = Ext.extend(Signavio.Core.DataStore, {getHeadRevision: function () {
        return this.getRecords("revision").sort(function (g, f) {
            return Number(g.get("rep").rev) - Number(f.get("rep").rev)
        }).last()
    }});
    Signavio.Core.UserGroupStore = Ext.extend(Signavio.Core.DataStore, {addAsParent: function (f) {
        if (!(f instanceof Ext.data.Record)) {
            warn("DataStore.addAsParent needs a contained record");
            return
        }
        var h = this.getIdentifier();
        var g = Signavio.Core.StoreManager.getStore(f.get("href"), true, function () {
            var j = g.getRecords("group").map(function (k) {
                return k.get("href")
            });
            if (!j.include(h)) {
                j.push(h);
                a.doSetGroupParents(f, j, this)
            }
        }.bind(this))
    }, removeAsParent: function (f) {
        if (!(f instanceof Ext.data.Record)) {
            warn("DataStore.removeAsParent needs a contained record");
            return
        }
        var h = f.get("href");
        var j = this.getIdentifier();
        var g = Signavio.Core.StoreManager.getStore(h, true, function () {
            var k = g.getRecords("group").map(function (l) {
                return l.get("href")
            });
            if (k.include(j)) {
                k = k.without(j);
                a.doSetGroupParents(f, k, this)
            }
        }.bind(this))
    }, commitChanges: function () {
        var g;
        if (this.removedRecords && this.removedRecords.length > 0) {
            var f = this.removedRecords.length - 1;
            for (; f >= 0; f--) {
                this.removeAsParent(this.removedRecords[f])
            }
            g = true
        }
        if (this.addedRecords && this.addedRecords.length > 0) {
            var f = this.addedRecords.length - 1;
            for (; f >= 0; f--) {
                this.addAsParent(this.addedRecords[f])
            }
            g = true
        }
        this.removedRecords = [];
        this.addedRecords = [];
        Signavio.Core.UserGroupStore.superclass.commitChanges.call(this);
        if (g) {
            this.fireEvent("load", this, this.data.items, this.lastOptions)
        }
    }, rejectChanges: function () {
        var h = this.addedRecords;
        var g = this.removedRecords;
        if (g && g.length > 0) {
            var f = g.length - 1;
            for (; f >= 0; f--) {
                this.add(g[f])
            }
        }
        if (h && h.length > 0) {
            var f = h.length - 1;
            for (; f >= 0; f--) {
                this.remove(h[f])
            }
        }
        this.removedRecords = [];
        this.addedRecords = [];
        Signavio.Core.UserGroupStore.superclass.rejectChanges.call(this);
        this.fireEvent("load", this, this.data.items, this.lastOptions)
    }})
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Core) {
    Signavio.Core = {}
}
var fn = function () {
};
new function () {
    var RM = Signavio.Core.Stores.RequestManager;
    Signavio.Core.Repository = {initialized: false, construct: function () {
        arguments.callee.$.construct.apply(this, arguments);
        this._controls = {};
        this._plugins = [];
        this._user = null;
        this._offered = [];
        this._finializeCount = 2;
        this._additionalFinializeCount = 0;
        this._additionalFinalClb = [];
        this._eventManager = new EventManager();
        this.showMask();
        this._initStores(this._initPlugins.bind(this));
        this._initComponents();
        this._initLanguage()
    }, getFacade: function () {
        return{getExtView: function (name) {
            return this._controls[name + "Panel"]
        }.bind(this), offer: function (option) {
            this._offered.push(option)
        }.bind(this), switchView: function (key) {
            this._eventManager.raiseEvent.call(this._eventManager, "viewswitch", key)
        }.bind(this), getTrashRecord: function () {
            var rootStore = Signavio.Core.StoreManager.getStore("root");
            return rootStore.findByRepValue("type", Signavio.Config.FOLDER_TYPE.TRASH)
        }.bind(this), getCurrentRootFolder: function () {
            var rootStore = Signavio.Core.StoreManager.getStore("root");
            var folder = this.getCurrentRootFolderStore();
            return rootStore.findByIdentifier(folder.getIdentifier())
        }.bind(this), getCurrentFolderStore: function () {
            return Signavio.Core.StoreManager.getStore("child")
        }.bind(this), getUserRecord: function (user) {
            return this.users.findByIdentifier(user)
        }.bind(this), getCurrentUser: function () {
            return this._user
        }.bind(this), getOwnerUser: function () {
            return this.users.findByIdentifier(this.config.owner)
        }.bind(this), isOwnerUser: function () {
            return this.config.user === this.config.owner
        }.bind(this), loadStencilSets: function (clb) {
            this.loadStencilSets(clb)
        }.bind(this), isCurrentUserInGroup: function (id) {
            return this._user && (this._user.getIdentifier() === id || this._user.getRecords(Signavio.Const.REL.GROUP).any(function (t) {
                return t.get("href") === id
            }))
        }.bind(this), isCurrentUserAdmin: function () {
            return this._user && this._user.getRecords("group").any(function (r) {
                return r.get("rep").isAdminGroup
            })
        }.bind(this), isExpired: function () {
            if (!this.isExpired) {
                this.isExpired = !(this._user.getRecords("info")[0].get("rep").licenses || []).any(function (r) {
                    var d = r.toLowerCase().gsub('"', "");
                    return d == Signavio.Config.TRIAL_LICENSE || d == Signavio.Config.TEAM_LICENSE || d == Signavio.Config.PREMIUM_LICENSE
                })
            }
            return this.isExpired
        }.bind(this), getConfig: function () {
            return Object.clone(this.config || {})
        }.bind(this), getUnusedLicenses: function (type) {
            return(this.config.licenses || []).findAll(function (l) {
                return !type || l.type.toLowerCase() === type.toLowerCase()
            })
        }.bind(this), setUnusedLicenses: function (licenses) {
            this.config.licenses = licenses || []
        }.bind(this), isSupportedBrowserEditor: function () {
            return !!(window.navigator.userAgent.match(this.config.supportedBrowserEditor))
        }.bind(this), isFirstStart: function () {
            return !!this._isFirstStart
        }.bind(this), askForRefresh: this.askForRefresh.bind(this), showAdministrationPanel: function (index) {
            var win;
            this._plugins.each(function (p) {
                if (p instanceof Signavio.Core.AdministrationPlugins) {
                    win = p.showTab(index || 0);
                    throw $break
                }
            });
            return win
        }.bind(this), raiseEvent: this._eventManager.raiseEvent.bind(this._eventManager), registerOnEvent: this._eventManager.registerOnEvent.bind(this._eventManager), unregisterOnEvent: this._eventManager.unregisterOnEvent.bind(this._eventManager), registerInitializeFinal: function (callback) {
            this._additionalFinializeCount += 1;
            this._additionalFinalClb.push(callback)
        }.bind(this), releaseInitializeFinal: function () {
            this.finialize()
        }.bind(this)}
    }, getCurrentRootFolderStore: function () {
        var rootStore = Signavio.Core.StoreManager.getStore("root");
        var currentStore = Signavio.Core.StoreManager.getStore("child");
        if (!rootStore || !currentStore) {
            return undefined
        }
        var all = Signavio.Core.StoreManager.getAllStores();
        var getParentStore = function (s) {
            return all.find(function (aa) {
                return !!aa.data.items.find(function (ar) {
                    return ar.get("href") === s.getIdentifier()
                })
            })
        };
        var root, croot = currentStore, i = 0;
        do {
            root = croot;
            croot = getParentStore(croot)
        } while (croot && croot != rootStore && i++ < all.length);
        return root
    }, loadStencilSets: function (clb) {
        if (!this.stencilsets) {
            Signavio.Core.Stores.RequestManager.doRequest(Signavio.Config.STENCILSET_URI, function (result) {
                result = result instanceof Array ? result : [];
                this.stencilsets = result;
                if (clb instanceof Function) {
                    clb(result.clone())
                }
            }.bind(this), {}, "get", false)
        } else {
            if (clb instanceof Function) {
                clb(this.stencilsets.clone())
            }
        }
    }, showMask: function () {
        var s = "background:white;bottom:0;height:100%;left:0;position:absolute;right:0;top:0;width:100%;z-index:100000;";
        var ss = "left:50%;margin-left:-200px;margin-top:-90px;position:absolute;top:50%;display:none;";
        var t = "<div class='mask-logo' style='" + ss + "'><div><img src='" + Signavio.Config.EXPLORER_PATH + "/src/img/signavio/signavio_logo.jpg'></div></div>";
        this.mask = Ext.get(document.createElement("div"));
        Ext.getBody().appendChild(this.mask);
        this.mask.addClass("x-mask");
        this.mask.addClass("x-background-white");
        this.mask.dom.setAttribute("style", s);
        this.mask.update(t);
        var loading = "<img style='' src='" + Signavio.Config.LIBS_PATH + "/ext-2.0.2/resources/images/default/tree/s.gif'/>";
        var t = this.mask.first().first().insertHtml("afterEnd", "<span class='mask-text'><span class='mask-title'>" + Signavio.Config.APPLICATION_NAME + "</span> <span class='mask-version'>Version " + Signavio.Core.Version + "</span>" + loading + "</span>", true);
        this.mask.first().show({duration: 0.3})
    }, hideMask: function () {
        window.setTimeout(function () {
            if (this.mask) {
                this.mask.first().hide({duration: 0.4, remove: true, block: true});
                this.mask.hide({duration: 0.3, remove: true, block: true});
                delete this.mask
            }
        }.bind(this), 1000)
    }, askForRefresh: function () {
        var fn = function (btn) {
            if (btn == "ok" || btn == "yes") {
                Signavio.Core.StoreManager.reloadChildEntitiesStore()
            }
        }.bind(this);
        var d = Ext.Msg.confirm("Signavio", Signavio.I18N.askForRefresh, fn);
        d.setIcon("x-window-info");
        d.getDialog().syncSize()
    }, _finialize: function () {
        if (--this._finializeCount > 0) {
            return
        }
        this._additionalFinalClb.each(function (clb) {
            if (clb instanceof Function) {
                clb.call(clb)
            }
        });
        if (this._additionalFinalClb <= 0) {
            this.finialize()
        }
    }, finialize: function () {
        if (this._finializeCount > 0) {
            return
        }
        if (--this._additionalFinializeCount > 0) {
            return
        }
        this._eventManager.raiseEvent("finialize");
        if (!this._plugins || this._plugins.length === 0) {
            this.noFeaturesAvailable()
        }
        this.hideMask()
    }, noFeaturesAvailable: function () {
        Ext.Msg.show({title: Signavio.I18N.Repository.noFeaturesTitle, msg: Signavio.I18N.Repository.noFeaturesMsg, icon: Ext.MessageBox.WARNING, closable: false});
        document.cookie = "JSESSIONID=0; expires=" + new Date().toGMTString() + "; path=/;"
    }, redirectToLogin: function () {
        window.location = Signavio.Config.LOGIN_PAGE
    }, _initStores: function (fn) {
        var store = Signavio.Core.StoreManager.getCurrentUser();
        store.on("load", this._initUser, this);
        var url = Signavio.Config.BACKEND_PATH + Signavio.Config.CONFIG_PATH;
        RM.doRequest(url, [function (result) {
            this.config = result;
            if (result && result.user) {
                Signavio.Core.StoreManager.loadCurrentUser(result.user);
                this.users = Signavio.Core.StoreManager.getAllUserStore();
                fn()
            } else {
                this.redirectToLogin()
            }
        }.bind(this), function (result) {
            Ext.Msg.show({title: "Signavio", msg: Signavio.I18N.Repository.loadingFailed, buttons: {ok: "Reload"}, fn: function () {
                window.location = ""
            }, icon: Ext.MessageBox.WARNING, width: 300}).getDialog().syncSize().el.setStyle("z-index", 1000000)
        }.bind(this)], null, "get")
    }, _initUser: function (store) {
        store.un("load", this._initUser, this);
        this._user = store;
        this._userName = store.getRecords("info")[0].get("rep").name || "";
        var isGuest = store.getRecords("info")[0].get("rep").isGuestUser || false;
//        Signavio.Templates.header.overwrite(this._controls.headerPanel.body, {user: this._userName, isTestSystem: this.config.isTestSystem, isGuest: isGuest});
//        if (isGuest) {
//            var form = this._controls.headerPanel.body.child("form.x-login");
//            if (form) {
//                form.dom.onsubmit = function (e) {
//                    Event.stop(e || window.event);
//                    var w = new Ext.Window({bodyStyle: "background:white;padding:10px;", cls: "x-window-login", layout: "anchor", modal: true, resizable: false, width: 325, title: Signavio.I18N.Repository.Header.loginTitle, items: [new Ext.form.FormPanel({border: false, anchor: "100% 100%", formId: "login", submit: function () {
//                        this.getForm().getEl().dom.setAttribute("action", Signavio.Config.LOGIN_PAGE);
//                        this.getForm().getEl().dom.submit()
//                    }, items: [new Ext.form.TextField({fieldLabel: Signavio.I18N.Repository.Header.windowFieldName, width: 290, tabIndex: 1000, name: "name", value: ($("name") || {}).value || "", autoCreate: {tag: "input", type: "text", size: "20"}, onRender: function (ct, position) {
//                        this.originParent = ($("name") || {}).parentNode;
//                        this.el = Ext.get($("name"));
//                        Ext.form.TextField.prototype.onRender.apply(this, arguments)
//                    }, beforeDestroy: function () {
//                        if (this.originParent) {
//                            this.originParent.appendChild(this.el.dom);
//                            this.rendered = false
//                        }
//                    }}), new Ext.form.TextField({fieldLabel: Signavio.I18N.Repository.Header.windowFieldPassword, width: 290, inputType: "password", tabIndex: 1001, name: "password", value: ($("password") || {}).value || "", autoCreate: {tag: "input", type: "password", size: "20"}, onRender: function (ct, position) {
//                        this.originParent = ($("password") || {}).parentNode;
//                        this.el = Ext.get($("password"));
//                        Ext.form.TextField.prototype.onRender.apply(this, arguments)
//                    }, beforeDestroy: function () {
//                        if (this.originParent) {
//                            this.originParent.appendChild(this.el.dom);
//                            this.rendered = false
//                        }
//                    }, listeners: {specialkey: function (e, k) {
//                        if (k.getKey() === k.ENTER) {
//                            this.ownerCt.ownerCt.buttons[1].handler()
//                        }
//                    }}}), new Ext.form.Checkbox({boxLabel: Signavio.I18N.Repository.Header.windowFieldRemember, labelSeparator: "", tabIndex: 1002, onRender: function (ct, position) {
//                        this.originParent = ($("remember") || {}).parentNode;
//                        this.el = Ext.get($("remember"));
//                        Ext.form.Checkbox.prototype.onRender.apply(this, arguments)
//                    }, beforeDestroy: function () {
//                        if (this.originParent) {
//                            this.originParent.appendChild(this.el.dom);
//                            this.rendered = false
//                        }
//                    }})]})], buttons: [new Ext.LinkButton({text: Signavio.I18N.Repository.Header.windowBtnResetPassword, click: function () {
//                        var ww = new Ext.Window({bodyStyle: "background:white;padding:10px;", cls: "x-window-login", layout: "form", modal: true, title: Signavio.I18N.Repository.Header.windowBtnResetPassword, items: [new Ext.form.Label({text: Signavio.I18N.Repository.Header.windowResetPasswordDesc, style: "width:200px;display:block;padding-bottom:10px;"}), new Ext.form.TextField({emptyText: Signavio.I18N.Repository.Header.windowFieldName, width: 200, hideLabel: true})], buttons: [
//                            {text: Signavio.I18N.Repository.Header.windowBtnSend, handler: function () {
//                                var mail = (this.ownerCt.items.get(1).getValue() || "").trim();
//                                if (!mail) {
//                                    return
//                                }
//                                RM.doPost(Signavio.Config.RESET_PASSWORD_PAGE, [function () {
//                                    ww.close();
//                                    Ext.Msg.alert(Signavio.I18N.Repository.Header.windowBtnResetPassword, Signavio.I18N.Repository.Header.windowResetPasswordHintOk).setIcon(Ext.Msg.INFO)
//                                }, function (e) {
//                                    Ext.Msg.alert(Signavio.I18N.Repository.Header.windowBtnResetPassword, Signavio.I18N.Repository.Header.windowResetPasswordHintFail).setIcon(Ext.Msg.WARNING)
//                                }], {mail: mail})
//                            }},
//                            {text: Signavio.I18N.Repository.Header.windowBtnCancel, handler: function () {
//                                this.ownerCt.close()
//                            }, tabIndex: 1004}
//                        ]});
//                        ww.show()
//                    }}), {text: Signavio.I18N.Repository.Header.windowBtnLogin, tabIndex: 1003, handler: function () {
//                        var form = this.ownerCt.items.get(0);
//                        var password = form.items.get(1);
//                        var name = form.items.get(0);
//                        if (!(password.getValue() || "").trim() || !(name.getValue() || "").trim()) {
//                            return
//                        }
//                        var params = {name: name.getValue(), password: password.getValue(), remember: form.items.get(2).getValue() ? "on" : undefined, tokenonly: true};
//                        RM.doPost(Signavio.Config.LOGIN_PAGE, [function (result) {
//                            form.submit()
//                        }, function (e) {
//                            var message = "";
//                            if (e.message) {
//                                message = e.message
//                            } else {
//                                message = Signavio.I18N.Repository.Header["loginError" + e.status]
//                            }
//                            Ext.Msg.alert(Signavio.I18N.Repository.Header.loginTitle, message).setIcon(Ext.Msg.WARNING)
//                        }], params)
//                    }}, {text: Signavio.I18N.Repository.Header.windowBtnCancel, handler: function () {
//                        this.ownerCt.close()
//                    }, tabIndex: 1004}], listeners: {show: function () {
//                        if (($("name") || {}).value) {
//                            window.setTimeout(this.buttons[1].focus.bind(this.buttons[1]), 10)
//                        } else {
//                            window.setTimeout(this.items.get(0).items.get(0).focus.bind(this.items.get(0).items.get(0)), 10)
//                        }
//                    }}}).show();
//                    return false
//                }
//            }
//        }
        var info = store.getRecords("info")[0];
        if (info.get("rep").isFirstStart) {
            var rep = Object.clone(info.get("rep"));
            rep.isFirstStart = false;
            info.set("rep", rep);
            info.commit();
            this._isFirstStart = true
        }
        this.getFacade().raiseEvent(Signavio.Const.EVENT.USER_LOADED);
        this._finialize()
    }, _initComponents: function () {
        Ext.QuickTips.init();
        Ext.apply(Ext.QuickTips.getQuickTip(), {showDelay: 1000, dismissDelay: false});
        this._controls.viewPanel = new Ext.Panel({region: "center", cls: "view_view", layout: "anchor", border: false, margins: {bottom: 63}});
        this._controls.leftPanel = new Ext.Panel({region: "west", cls: "left_view", collapsible: true, collapsed: false, split: true, width: 250, border: false, hidden: !!Signavio.Config.HideLeftPanel, autoScroll: true});
        this._controls.rightPanel = new Ext.Panel({region: "east", cls: "right_view", title: Signavio.I18N.Repository.rightPanelTitle, header: false, layout: "card", collapsible: true, collapsed: true, collapseMode: "mini", split: true, width: 290, autoScroll: true, plugins: [new Signavio.Extensions.DrawerButtons()]});
        this._controls.bottomPanel = new Ext.Panel({region: "south", cls: "bottom_view", collapsible: true, title: "", border: false});
        this._controls.toolbarPanel = new Ext.Toolbar({region: "south", cls: "north_view", border: false, height: 28});
        //this._controls.headerPanel = new Ext.Panel({region: "center", cls: "header_view", html: Signavio.Templates.header.apply({user: "", isTestSystem: false}), border: false});
        this._controls.topPanel = new Ext.Panel({region: "north", layout: "border", cls: "north_view", height: 28, border: false, items: [this._controls.toolbarPanel]});  //this._controls.headerPanel, 63
        this._controls.viewport = new Ext.Viewport({layout: "border", margins: "0 0 0 0", defaults: {}, border: false, items: [this._controls.topPanel, this._controls.viewPanel, this._controls.leftPanel]});
        this._controls.viewport.doLayout();
        this._controls.hoverPanel = {height: 70, facade: this.getFacade(), items: []}
    }, _initLanguage: function () {
        var lang = Signavio.I18N.Language;
        document.documentElement.className += " " + lang.split("_").concat(lang).uniq().join(" ");
        var browser = Ext.isIE7 ? "x-ie x-ie7" : (Ext.isIE ? "x-ie" : (Ext.isGecko ? "x-gecko" : "x-other"));
        var version = Signavio.Helper.getBrowserVersion();
        document.documentElement.className += " " + browser + " " + browser + "-" + version.replace(/\./g, "-")
    }, _initHoverPanel: function () {
        var plugins = this._controls.hoverPanel.items;
        if (plugins.length > 0) {
            var o = this._controls.hoverPanel;
            o.items = o.items.map(function (e) {
                return e.panel
            });
            this._controls.hoverPanel = new Signavio.Extensions.HoverPanel(o);
            this._controls.hoverPanel.doLayout();
            plugins.each(function (p) {
                p.parent = this._controls.hoverPanel;
                p.afterRender()
            }.bind(this))
        }
    }, _initPlugins: function () {
        var source = Signavio.Config.PLUGIN_CONFIG;
        RM.doRequest(source, [function (result) {
            var resultXml = result.responseXML;
            var plugins = $A(resultXml.getElementsByTagName("plugin")).map(function (p) {
                return{name: p.getAttribute("name"), source: p.getAttribute("source")}
            }).compact();
            this._registerPlugins(plugins);
            this._finialize()
        }.bind(this), function (e) {
            if (e.status === 401) {
                this.redirectToLogin()
            }
        }.bind(this)], null, "get", true)
    }, _registerPlugins: function (plugins) {
        var facade = this.getFacade();
        plugins.each(function (pl) {
            info("Initialize plugin " + pl.name + ".");
            try {
                if (!pl.name || pl.name.strip().length <= 0) {
                    return
                }
                var className = eval(pl.name);
                if (className.prototype.enabled) {
                    var plugin = new className(facade);
                    this._plugins.push(plugin);
                    info("Plugin " + pl.name + " successfully loaded.")
                } else {
                    info("Plugin " + pl.name + " is not enabled.")
                }
            } catch (e) {
                warn("Plugin " + pl.name + " could not been instanziated.\n" + e)
            }
        }.bind(this));
        info("Finialize the loading of plugins.");
        this._initHoverPanel();
        this.getFacade().switchView(Signavio.Config.INITIAL_VIEW);
        this._plugins.each(function (plugin) {
            if (plugin.handleOffers instanceof Function) {
                plugin.handleOffers(this._offered)
            }
        }.bind(this))
    }, _appendJSFiles: function (plugins, callback) {
        var prefixURL = Signavio.Config.PLUGIN_PATH + "/";
        plugins.each(function (plugin) {
            try {
                var className = eval(plugin.name);
                if (className) {
                    callback(plugin);
                    return
                }
            } catch (e) {
            }
            var head = document.getElementsByTagName("head")[0];
            var s = document.createElement("script");
            s.setAttribute("type", "text/javascript");
            s.src = prefixURL + plugin.source;
            if (navigator.product == "Gecko") {
                s.onload = callback.bind(callback, plugin)
            } else {
                s.onreadystatechange = callback.bind(callback, plugin)
            }
            head.appendChild(s)
        })
    }};
    Signavio.Core.Repository = Clazz.extend(Signavio.Core.Repository);
    var EventManager = Clazz.extend({construct: function () {
        this.callbacks = {}
    }, raiseEvent: function (type) {
        if (type && this.callbacks[type] instanceof Array) {
            var arg = arguments;
            this.callbacks[type].each(function (callback) {
                window.setTimeout(function () {
                    callback.apply(callback, arg)
                }, 10)
            })
        }
    }, registerOnEvent: function (type, callback) {
        if (callback instanceof Function) {
            if (!this.callbacks[type]) {
                this.callbacks[type] = []
            }
            this.callbacks[type].push(callback)
        }
    }, unregisterOnEvent: function (type, callback) {
        if (callback instanceof Function && this.callbacks[type] && this.callbacks.include(callback)) {
            delete this.callbacks[type][this.callbacks[type].indexOf(callback)]
        }
    }});
    Signavio.Core.EventManager = EventManager
}();
Signavio.Core.graft = function (h, g, f, l) {
    l = (l || (g && g.ownerDocument) || document);
    var j;
    if (f === undefined) {
        throw"Can't graft an undefined value"
    } else {
        if (f.constructor == String) {
            j = l.createTextNode(f)
        } else {
            for (var d = 0; d < f.length; d++) {
                if (d === 0 && f[d].constructor == String) {
                    var a;
                    a = f[d].match(/^([a-z][a-z0-9]*)\.([^\s\.]+)$/i);
                    if (a) {
                        j = l.createElementNS instanceof Function ? l.createElementNS(h, a[1]) : l.createElement(a[1]);
                        j.setAttribute("class", a[2]);
                        continue
                    }
                    a = f[d].match(/^([a-z][a-z0-9]*)$/i);
                    if (a) {
                        j = l.createElement(a[1]);
                        continue
                    }
                    j = l.createElement("span");
                    j.setAttribute(null, "class", "namelessFromLOL")
                }
                if (f[d] === undefined) {
                    throw"Can't graft an undefined value in a list!"
                } else {
                    if (f[d].constructor == String || f[d].constructor == Array) {
                        this.graft(h, j, f[d], l)
                    } else {
                        if (f[d].constructor == Number) {
                            this.graft(h, j, f[d].toString(), l)
                        } else {
                            if (f[d].constructor == Object) {
                                for (var b in f[d]) {
                                    j.setAttribute(b, f[d][b]);
                                    if (b === "class") {
                                        j.className = f[d][b]
                                    }
                                }
                            } else {
                            }
                        }
                    }
                }
            }
        }
    }
    if (g) {
        g.appendChild(j)
    } else {
    }
    return j
};
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Plugins) {
    Signavio.Plugins = {}
}
var abStore;
new function () {
    Signavio.Plugins.Search = {renderToView: "toolbar", enabled: true, render: function () {
        this.panel = new Ext.Panel({cls: "search_plugin", border: false, layout: "table", layoutConfig: {columns: 3}, items: [new Ext.Panel({cls: "search_field_left", style: "cursor:pointer;", width: 22, heigt: 22, border: false, listeners: {render: function (d) {
            d.el.on("click", this.doSearch, this)
        }.bind(this)}}), new Ext.form.TextField({value: "", id: "search_plugin_field", emptyText: Signavio.I18N.Repository.Offer.search, width: 100, listeners: {specialkey: function (d, f) {
            if (f.getKey() == f.RETURN) {
                this.doSearch();
                d.blur();
                f.preventDefault()
            }
        }.bind(this), focus: function () {
            this.panel.items.get(0).el.addClass("x-focus");
            this.panel.items.get(1).el.parent().addClass("x-focus");
            this.panel.items.get(2).el.addClass("x-focus");
            this.panel.body.addClass("x-focus")
        }.bind(this), blur: function () {
            this.panel.items.get(0).el.removeClass("x-focus");
            this.panel.items.get(1).el.parent().removeClass("x-focus");
            this.panel.items.get(2).el.removeClass("x-focus");
            this.panel.body.removeClass("x-focus")
        }.bind(this)}}), new Ext.Panel({cls: "search_field_right", width: 10, heigt: 22, border: false})]});
        var a = Signavio.Core.StoreManager.getRelatedStore(null, "child");
        a.on("load", function (d) {
            if (d.lastOptions.url.endsWith("search")) {
                this.panel.addClass("activated");
                this.panel.items.get(1).setValue(d.lastOptions.params.q || "")
            } else {
                this.panel.removeClass("activated")
            }
        }.bind(this));
        var b = function (e, d) {
            this.panel.items.get(1).setWidth(Math.max(d - 74, 45))
        }.bind(this);
        this.facade.getExtView("left").on("resize", b);
        b(null, this.facade.getExtView("left").width)
    }, afterRender: function () {
        this.parent.addSeparator()
    }, doSearch: function () {
        var a = Ext.getCmp("search_plugin_field").getValue();
        if (a.length == 0) {
            return
        }
        Signavio.Core.StoreManager.getRelatedStore("/search", "child", ["mod", "dir"], {force: true, params: {q: a, offset: 0, limit: Signavio.Config.SEARCH_LIMIT}});
        this.facade.raiseEvent("searchactivated", a);
        this.panel.addClass("activated")
    }};
    Signavio.Plugins.Search = Signavio.Core.ComponentPlugin.extend(Signavio.Plugins.Search)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Plugins) {
    Signavio.Plugins = {}
}
new function () {
    Signavio.Plugins.New = {enabled: true, construct: function () {
        arguments.callee.$.construct.apply(this, arguments);
        var a = function () {
            return !this.facade.getCurrentFolderStore() || this.selectionInSearch() || !this.facade.getCurrentFolderStore().hasPrivileges(Signavio.Config.RIGHTS.WRITE)
        }.bind(this);
        this.facade.offer({cls: "x-menu-large", group: "new", name: Signavio.I18N.Repository.Offer.newTitle, icon: Signavio.Config.EXPLORER_PATH + "/src/img/nuvola/16x16/actions/filenew.png", isVisible: function () {
            return !this.selectionInTrash() && !this.selectionInSearch()
        }.bind(this), isDisabled: a, items: [
            {name: Signavio.I18N.Repository.Offer.newFolderTitle, description: Signavio.I18N.Repository.Offer.newFolderDescription, seperated: true, activeClass: "x-menu-item-active item-large", iconCls: "x-menu-item-icon-large", cls: "x-menu-list-item-large", icon: Signavio.Config.EXPLORER_PATH + "/src/img/signavio/new_folder.png", functionality: this.createFolder.bind(this)}
        ], onAdd: function (d) {
            this.menuButton = d;
            if (this.cacheButtons) {
                this.cacheButtons.each(function (e) {
                    d.menu.addMenuItem(e)
                })
            }
        }.bind(this)});
        if (this.facade.isSupportedBrowserEditor()) {
            this.facade.offer({relGroup: "edit", index: 0, name: Signavio.I18N.Repository.Offer.edit, description: Signavio.I18N.Repository.Offer.editDescription, icon: Signavio.Config.EXPLORER_PATH + "/src/img/nuvola/16x16/actions/pencil.png", isDisabled: function () {
                return this.selectionInTrash() || !this.oneSelected()
            }.bind(this), neededPrivileges: [Signavio.Config.RIGHTS.WRITE], functionality: function () {
                var d = Signavio.Config.EDITOR_HANDLER_URI + "?" + Object.toQueryString({id: this.getSelection()[0].get("href").split("/")[2]});
                window.open(d)
            }.bind(this)})
        }
        var b = Signavio.I18N.Language.split("_").first();
        this.facade.loadStencilSets(function (d) {
            if (!this.menuButton) {
                this.menuButton = this.facade.getExtView("toolbar").items.items.find(function (e) {
                    return e.offer && e.offer.group == "new"
                })
            }
            d.each(function (e) {
                if (e.visible === false) {
                    return
                }
                var f = {text: e["title_" + b] || e.title, tooltip: e["description_" + b] || e.description, activeClass: "x-menu-item-active item-large", iconCls: "x-menu-item-icon x-menu-item-icon-large", cls: "x-menu-list-item-large", icon: Signavio.Config.EXPLORER_PATH + "/src/img" + e.icon_url, handler: this.createModel.bind(this, Signavio.Config.BACKEND_PATH + "/editor?stencilset=" + e.namespace.replace(/#/g, "%23"))};
                if (!this.menuButton) {
                    if (!this.cacheButtons) {
                        this.cacheButtons = []
                    }
                    this.cacheButtons.push(f)
                } else {
                    this.menuButton.menu.addMenuItem(f)
                }
            }.bind(this))
        }.bind(this))
    }, afterRender: function () {
    }, createFolder: function () {
        var a = Ext.Msg.prompt(Signavio.I18N.Repository.Offer.newWindowFolderTitle, Signavio.I18N.Repository.Offer.newWindowFolderDesc,function (d, f) {
            if (d === "ok") {
                f = f.trim();
                if (f.length === 0) {
                    f = Signavio.I18N.Repository.Offer.newFolderDefaultTitle
                }
                var b = Signavio.Core.StoreManager.getStore("child");
                var e = Signavio.Core.RecordCreator.create(Signavio.Const.REL.DIRECTORY, null, {name: f, description: ""});
                b.add(e, true)
            }
        }, this, false, Signavio.I18N.Repository.Offer.newFolderDefaultTitle).getDialog();
        window.setTimeout(a.focusEl.focus, 100)
    }, createModel: function (b) {
        if (this.facade.isSupportedBrowserEditor()) {
            var a = Signavio.Core.StoreManager.getStore("child");
            window.open(b + "&directory=" + a.lastOptions.id);
            this.facade.askForRefresh()
        } else {
            var e = Ext.Msg.alert("Signavio", Signavio.I18N.Repository.Offer.newFFOnly);
            e.setIcon("x-window-ffonly")
        }
    }};
    Signavio.Plugins.New = Signavio.Core.Plugin.extend(Signavio.Plugins.New)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Plugins) {
    Signavio.Plugins = {}
}
new function () {
    Signavio.Plugins.Update = {enabled: true, construct: function () {
        arguments.callee.$.construct.apply(this, arguments);
        this.facade.offer({name: Signavio.I18N.Repository.Offer.updateTitle, arrange: "right", description: Signavio.I18N.Repository.Offer.updateDescription, icon: Signavio.Config.EXPLORER_PATH + "/src/img/nuvola/16x16/actions/reload.png", functionality: this.update.bind(this)})
    }, update: function () {
        Signavio.Core.StoreManager.reload()
    }};
    Signavio.Plugins.Update = Signavio.Core.Plugin.extend(Signavio.Plugins.Update)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Plugins) {
    Signavio.Plugins = {}
}
new function () {
    Signavio.Plugins.Delete = {enabled: true, construct: function () {
        arguments.callee.$.construct.apply(this, arguments);
        this.facade.offer({relGroup: "edit", index: 6, name: Signavio.I18N.Repository.Offer.deleteTitle, description: Signavio.I18N.Repository.Offer.deleteDescription, icon: Signavio.Config.EXPLORER_PATH + "/src/img/famfamfam/page_white_remove.png", functionality: this.deleteElements.bind(this), isDisabled: function () {
            return this.noneSelected()
        }.bind(this), isVisible: this.selectionNotInTrash.bind(this), seperated: true, neededPrivileges: [Signavio.Config.RIGHTS.DELETE]});
        this.facade.offer({name: Signavio.I18N.Repository.Offer.removeTitle, description: Signavio.I18N.Repository.Offer.removeDescription, icon: Signavio.Config.EXPLORER_PATH + "/src/img/famfamfam/page_white_remove.png", functionality: this.removeElements.bind(this), isDisabled: function () {
            return this.noneSelected()
        }.bind(this), isVisible: this.selectionInTrash.bind(this), neededPrivileges: [Signavio.Config.RIGHTS.DELETE]});
        this.facade.offer({name: Signavio.I18N.Repository.Offer.restoreTitle, description: Signavio.I18N.Repository.Offer.restoreDescription, icon: Signavio.Config.EXPLORER_PATH + "/src/img/nuvola/16x16/actions/restore.png", functionality: this.recoverElements.bind(this), isDisabled: function () {
            return this.noneSelected()
        }.bind(this), isVisible: this.selectionInTrash.bind(this), neededPrivileges: [Signavio.Config.RIGHTS.DELETE]})
    }, deleteElements: function () {
        if (Signavio.Config.REMOVE_ON_DELETE) {
            this.removeElements();
            return
        }
        var a = function () {
            this.getSelection().each(function (b) {
                var d = b.store.getIdentifier();
                b.get("rep").parent = d;
                b.store.move(b, this.facade.getTrashRecord().get("href"))
            }.bind(this))
        }.bind(this);
        this.confirm(Signavio.I18N.Repository.Offer.deleteQuestion, a)
    }, removeElements: function () {
        var a = function () {
            this.getSelection().each(function (b) {
                b.store.remove(b, true)
            }.bind(this))
        }.bind(this);
        this.confirm(Signavio.I18N.Repository.Offer.removeQuestion, a)
    }, recoverElements: function () {
        this.getSelection().each(function (a) {
            var b = a.get("rep").parent;
            if (b) {
                delete a.get("rep").parent;
                a.store.move(a, b)
            }
        }.bind(this))
    }};
    Signavio.Plugins.Delete = Signavio.Core.Plugin.extend(Signavio.Plugins.Delete)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Plugins) {
    Signavio.Plugins = {}
}
new function () {
    Signavio.Plugins.Move = {enabled: true, construct: function () {
        arguments.callee.$.construct.apply(this, arguments);
        this.facade.offer({group: "edit", name: Signavio.I18N.Repository.Offer.editGroupTitle, icon: Signavio.Config.EXPLORER_PATH + "/src/img/nuvola/16x16/apps/package_utilities.png", isVisible: this.selectionNotInTrash.bind(this), items: [
            {relGroup: "edit", index: 3, seperated: true},
            {relGroup: "edit", index: 4, name: Signavio.I18N.Repository.Offer.moveTitle, description: Signavio.I18N.Repository.Offer.moveDescription, icon: Signavio.Config.EXPLORER_PATH + "/src/img/famfamfam/page_white_go.png", functionality: this.doMove.bind(this, true), isDisabled: function () {
                return this.selectionInTrash() || this.selectionInSearch() || this.noneSelected()
            }.bind(this), neededPrivileges: [Signavio.Config.RIGHTS.DELETE]},
            {relGroup: "edit", index: 5, name: Signavio.I18N.Repository.Offer.copyTitle, description: Signavio.I18N.Repository.Offer.copyDescription, icon: Signavio.Config.EXPLORER_PATH + "/src/img/famfamfam/page_white_copy.png", functionality: this.doMove.bind(this, false), isDisabled: function () {
                return this.selectionInTrash() || !this.manySelected()
            }.bind(this), neededPrivileges: [Signavio.Config.RIGHTS.READ]}
        ]})
    }, doMove: function (a) {
        var b = function (f, g, d) {
            if (!f) {
                return
            }
            var e = Signavio.Core.StoreManager.getStore(f.get("href"), true, function () {
                var h = e.hasPrivileges(Signavio.Config.RIGHTS.WRITE);
                if (!h) {
                    Ext.Msg.alert(Signavio.I18N.Repository.Offer[(g ? "copy" : "move") + "AlertTitle"], Signavio.I18N.Repository.Offer[(g ? "copy" : "move") + "AlertDesc"]).setIcon(Ext.Msg.INFO).getDialog().syncSize()
                } else {
                    var j = this.facade.getCurrentFolderStore();
                    this.getSelection().each(function (k) {
                        if (g) {
                            var l = k.copy();
                            l.get("rep").name = l.get("rep").name + Signavio.I18N.Repository.Offer.copyPrefix;
                            j.copy(l, f.get("href"))
                        } else {
                            if (k.store.getIdentifier() !== f.get("href")) {
                                j.move(k, f.get("href"))
                            }
                        }
                    }.bind(this));
                    d.close()
                }
            }.bind(this))
        }.bind(this);
        this.showWindow(b, a)
    }, showWindow: function (j, b) {
        var h;
        if (this.getSelection().length === 1) {
            h = new Template(Signavio.I18N.Repository.Offer[(b ? "move" : "copy") + "WindowHeader"]).evaluate({title: this.getSelection()[0].get("rep").name})
        } else {
            h = new Template(Signavio.I18N.Repository.Offer[(b ? "move" : "copy") + "WindowHeaderMultiple"]).evaluate({count: this.getSelection().length})
        }
        var f = this.getSelection().all(function (k) {
            return k.get("rel") === Signavio.Const.REL.MODEL
        });
        var d = Signavio.Core.StoreManager.getRootDirectoryStore();
        d.lastOptions.sort = function () {
            return 0
        };
        var e = this.getSelection().length == 1 ? this.getSelection()[0] : this.facade.getCurrentFolderStore();
        var g = new Signavio.Extensions.ModelDirectoryCheckTreePanel({height: 270, style: "padding:0px;", disabledIds: this.getSelection().map(function (k) {
            return k.get("href")
        }), root: new Ext.tree.TreeNode({text: "", draggable: false, leaf: false, cls: "folder", childCls: "folder", dataField: "rep.name", identifier: Signavio.Config.DIRECTORY_PATH, expanded: true, recordRel: Signavio.Const.REL.DIRECTORY, filterFn: function (k) {
            if (k.get("rel") == "mod") {
                return true
            } else {
                if (k.get("rel") == "dir") {
                    return !this.isTrashFolder(k)
                } else {
                    return false
                }
            }
        }.bind(this)})}, [e].compact());
        g.loader = new Signavio.Extensions.TreeLoader({panel: g, sortingFn: g.sortingFn, generateNodeData: function (m, p, n) {
            var l = Signavio.Extensions.TreeLoader.prototype.generateNodeData.apply(this, arguments);
            var k = m.hasPrivileges(Signavio.Config.RIGHTS.WRITE);
            n.ui[k ? "removeClass" : "addClass"]("x-disabled");
            n.ui[k ? "removeClass" : "addClass"]("x-locked");
            window.setTimeout(function () {
                l.each(function (s, q) {
                    var r = Signavio.Core.StoreManager.getStore(s.identifier, true, function () {
                        var t = r.hasPrivileges(Signavio.Config.RIGHTS.WRITE);
                        if (!t) {
                            var u = n.childNodes.find(function (v) {
                                return v.attributes.identifier === s.identifier
                            });
                            if (u) {
                                u.ui.addClass("x-disabled");
                                u.ui.addClass("x-locked")
                            }
                        }
                    })
                })
            }, 1);
            return l
        }});
        var a = new Ext.Window({modal: true, cls: "x-plugin-move-window", title: Signavio.I18N.Repository.Offer[(b ? "move" : "copy") + "Title"], resizable: false, width: 400, items: [new Ext.form.Label({text: h}), g], buttons: [
            {text: Signavio.I18N.Repository.Offer[(b ? "move" : "copy") + "BtnOk"], handler: function () {
                var k = a.items.get(1).getSelectedRecord();
                var l = !b;
                j(k, l, a)
            }},
            {text: Signavio.I18N.Repository.Offer.moveBtnCancel, handler: function () {
                a.close()
            }}
        ]});
        a.show()
    }, isTrashFolder: function (a) {
        return this.facade.getTrashRecord() && a.get("href") == this.facade.getTrashRecord().get("href")
    }};
    Signavio.Plugins.Move = Signavio.Core.Plugin.extend(Signavio.Plugins.Move)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Plugins) {
    Signavio.Plugins = {}
}
new function () {
    Signavio.Plugins.Folder = {renderToView: "left", enabled: true, render: function () {
        var a = new Ext.tree.TreeNode({text: "", identifier: "", draggable: false, expanded: true});
        this.panel = new Ext.tree.TreePanel({id: "folder_plugin", animate: true, enableDD: false, pathSeparator: "#", useArrows: true, loader: new Signavio.Extensions.TreeLoader({showTooltip: true}), lines: false, border: false, rootVisible: false, containerScroll: false});
        this.panel.getSelectionModel().on("selectionchange", this.onSelectionChange.bind(this));
        this.panel.getSelectionModel().on("beforeselect", function (d, e) {
            var f = this.panel.getLoader().findRecord(e.attributes.identifier) || Signavio.Core.StoreManager.getRootDirectoryStore().findByIdentifier(e.attributes.identifier);
            if (f && f.get("rel") === Signavio.Const.REL.GLOSSARY) {
                window.open(f.get("rep").uri);
                return false
            }
            return true
        }.bind(this));
        this.panel.setRootNode(a);
        this.folders = Signavio.Core.StoreManager.getRootDirectoryStore(this.onFoldersUpdate.bind(this));
        this.facade.registerOnEvent("searchactivated", function () {
            this.panel.selectPath("")
        }.bind(this));
        var b = Signavio.Core.StoreManager.getChildEntitiesStore();
        b.on("load", this.onChildFolderChange.bind(this))
    }, onFoldersUpdate: function (b, g) {
        var a = this.folders;
        var e = b.map(function (j) {
            var l = j.get("rel") === Signavio.Const.REL.GLOSSARY;
            if (l && !Signavio.Config.GLOSSARY_ENABLED) {
                return undefined
            }
            var k = l ? Signavio.I18N.Repository.Offer.glossaryTitle + " <img src='/explorer/src/img/nuvola/16x16/actions/arrow.png'/>" : (Signavio.I18N.Repository.Folder[j.get("rep").type] || j.get("rep").name);
            return j.get("rep").type == Signavio.Config.FOLDER_TYPE.TRASH && j.get("rep").visible === false ? undefined : {text: k, type: j.get("rep").type, cls: "folder", leaf: false, identifier: j.get("href"), rel: j.get("rel"), cls: l ? "x-glossary" : "folder", dataField: "rep.name", leaf: j.get("rep").type == "trash" || l, isChildLeaf: function (m) {
                return m.get("rel") !== Signavio.Const.REL.DIRECTORY
            }, hasChildCls: function (m) {
                return m.get("rel") == Signavio.Const.REL.DIRECTORY ? "folder" : "x-glossary"
            }, getToolTip: function (m) {
                return m.get("rel") == Signavio.Const.REL.DIRECTORY ? "" : Signavio.I18N.Repository.Offer.glossaryDescription
            }, parseText: function (n, m) {
                return m.get("rel") === Signavio.Const.REL.DIRECTORY ? n : Signavio.I18N.Repository.Offer.glossaryTitle + " <img src='/explorer/src/img/nuvola/16x16/actions/arrow.png'/>"
            }, recordRel: [Signavio.Const.REL.DIRECTORY, Signavio.Const.REL.GLOSSARY]}
        }).compact();
        (e.find(function (j) {
            return j.type == Signavio.Config.FOLDER_TYPE.TRASH
        }) || {}).cls = "trash";
        var f = Ext.get(this.panel.getRootNode().ui.ctNode);
        f.hide();
        var h = this.panel.getRootNode();
        var d = h.childNodes.findAll(function (j) {
            return j.attributes.retain || false
        });
        this.removeChildNodes(h);
        e = e.map(function (j) {
            return new Ext.tree.AsyncTreeNode(j)
        });
        h.appendChild(e);
        d.each(function (j) {
            h.appendChild(new Ext.tree.TreeNode(j.attributes))
        });
        f.show();
        f.slideIn("t");
        window.setTimeout(function () {
            if (this.finialized && !this.panel.getSelectionModel().getSelectedNode() && !this.currentChildStore && this.panel.root.firstChild && this.panel.root.firstChild.attributes.rel === Signavio.Const.REL.DIRECTORY) {
                this.panel.root.item(0).select()
            }
        }.bind(this), 500);
        a.un("load", this.onFoldersUpdate)
    }, finialize: function () {
        this.finialized = true;
        if (!this.currentChildStore && !this.panel.getSelectionModel().getSelectedNode() && this.panel.root.childNodes.length > 0 && this.panel.root.item(0).attributes.rel !== Signavio.Const.REL.GLOSSARY) {
            this.panel.root.item(0).select()
        }
    }, getSelectionPath: function (a) {
        var b = [];
        var d = a || this.panel.getSelectionModel().getSelectedNode();
        while (d.parentNode && d !== this.panel.root) {
            b.unshift({identifier: d.attributes.identifier, name: d.attributes.text, store: Signavio.Core.StoreManager.getStore(d.attributes.identifer)});
            d = d.parentNode
        }
        return b
    }, onSelectionChange: function (a, b) {
        if (b && b.attributes.identifier) {
            this.facade.raiseEvent(Signavio.Const.EVENT.FOLDER_SELECTION_CHANGE, this.getSelectionPath(b));
            Signavio.Core.StoreManager.loadChildEntitiesStore(b.attributes.identifier)
        } else {
            return false
        }
    }, onChildFolderChange: function (j) {
        this.currentChildStore = j;
        var a = j.getIdentifier();
        var b = this.panel.getSelectionModel();
        var d = b.getSelectedNode();
        if ((d && d.attributes.identifier === a) || j.getIdentifier().startsWith("/search")) {
            return
        }
        var h = !!this.finialized;
        var e = j.getRecords("parents")[0];
        if (!e) {
            return
        }
        var k = e.get("rep").pluck("href");
        k.unshift(a);
        var f = function (l) {
            if (l.attributes.identifier === j.getIdentifier()) {
                if (!b.isSelected(l) && b.selNode != l) {
                    if (b.selNode) {
                        b.selNode.ui.onSelectedChange(false)
                    }
                    b.selNode = l;
                    l.ui.onSelectedChange(true);
                    this.facade.raiseEvent(Signavio.Const.EVENT.FOLDER_SELECTION_CHANGE, this.getSelectionPath(l))
                }
            }
        }.bind(this);
        var g = function (m) {
            if (!m) {
                return
            }
            f(m);
            var l = m.childNodes.find(function (n) {
                return k.include(n.attributes.identifier)
            });
            if (l) {
                f(l);
                if (!l.isExpanded()) {
                    l.expand(false, h, function () {
                        g(l)
                    })
                } else {
                    g(l)
                }
            }
        }.bind(this);
        g(this.panel.root)
    }, appendChildNodes: function (b, d) {
        d = d instanceof Array ? d : [d];
        var a = d.map(function (e) {
            return new Ext.tree.TreeNode(e)
        });
        b.appendChild(a)
    }, removeChildNodes: function (b) {
        var a = b.childNodes.length - 1;
        for (; a >= 0; a--) {
            b.childNodes[a].remove()
        }
    }};
    Signavio.Plugins.Folder = Signavio.Core.ComponentPlugin.extend(Signavio.Plugins.Folder)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Plugins) {
    Signavio.Plugins = {}
}
new function () {
    Signavio.Plugins.Toolbar = {renderToView: "toolbar", enabled: true, relTypes: [Signavio.Const.REL.PRIVILEGE], construct: function () {
        arguments.callee.$.construct.apply(this, arguments);
        this.facade.registerOnEvent("selectionchange", this.checkButtonState.bind(this));
        (Signavio.Core.StoreManager.getStore("child") || Signavio.Core.StoreManager.getChildEntitiesStore()).on("load", this.checkButtonState.bind(this));
        Ext.EventManager.onWindowResize(this.updateResize, this);
        this.facade.getExtView("left").on("resize", this.updateResize, this)
    }, finialize: function () {
        this.updateResize()
    }, hasClass: function (el, cls) {
        if (el instanceof Ext.Toolbar.Button) {
            if (el.menu instanceof Ext.menu.Menu) {
                return el.menu.el.hasClass(cls) || el.el.hasClass(cls)
            } else {
                return el.el.hasClass(cls)
            }
        } else {
            if (el.el.className) {
                return el.el.className.include(cls)
            } else {
                return false
            }
        }
    }, getWidth: function (r) {
        return r.el.dom ? r.el.getWidth() : r.el.offsetWidth
    }, getMinWidth: function (r) {
        if (r.hidden || r.el.className === "ytb-spacer") {
            return 0
        } else {
            if (this.hasClass(r, "x-arrange-left") || this.hasClass(r, "x-arrange-right")) {
                return r.menu ? 32 : 22
            } else {
                var node = (r.el.parentNode || r.el.parent());
                node = node.dom ? node.dom : node;
                return node.offsetWidth
            }
        }
    }, swapClass: function (button, oldClass, newClass) {
        button.el.removeClass(oldClass);
        button.el.addClass(newClass)
    }, updateResize: function () {
        if (!this.buttonsWidth) {
            this.buttonsWidth = this.parent.items.items.map(function (r, i) {
                return this.getWidth(r)
            }.bind(this))
        }
        var width = this.parent.container.getWidth();
        var minButtonWidth = eval(this.parent.items.items.map(function (r) {
            return this.getMinWidth(r)
        }.bind(this)).join("+"));
        this.parent.items.items.each(function (button, i) {
            if (!button.hidden && (this.hasClass(button, "x-arrange-left") || this.hasClass(button, "x-arrange-right"))) {
                var min = this.getMinWidth(button);
                if (width - min > minButtonWidth - min + this.buttonsWidth[i]) {
                    this.swapClass(button, "x-btn-icon", "x-btn-text-icon");
                    minButtonWidth = minButtonWidth - min + this.buttonsWidth[i]
                } else {
                    this.swapClass(button, "x-btn-text-icon", "x-btn-icon");
                    width = 0
                }
            }
        }.bind(this))
    }, render: function () {
    }, handleOffers: function (options) {
        if (!options) {
            return
        }
        var menus = $H({});
        options = options.partition(function (a) {
            return !a.arrange || a.arrange !== "right"
        });
        options.each(function (opt, index) {
            if (index === 1) {
                this.parent.addFill()
            }
            var cls = index === 0 ? "x-arrange-left" : "x-arrange-right";
            var menuNames = opt.pluck("group").compact().uniq();
            opt.each(function (offer) {
                if (offer && offer.relGroup && menuNames.include(offer.relGroup)) {
                    return
                }
                if (offer.group) {
                    if (!menus.get(offer.group)) {
                        var menu = new Ext.menu.Menu({items: [], cls: (offer.cls || "")});
                        var menuItem = {iconCls: "class_bugfixing", text: offer.name, menu: menu, disabled: true, icon: offer.icon, offer: offer, cls: cls + " x-btn-text-icon"};
                        this.parent.add(menuItem);
                        offer.button = menu;
                        menus.set(offer.group, menu)
                    }
                    offer.items = offer.items || offer;
                    var menu = menus.get(offer.group);
                    var items = offer.items;
                    items = items.concat(opt.findAll(function (r) {
                        return r && r.relGroup && r.relGroup == offer.group
                    }));
                    items = items.sort(function (a, b) {
                        return a.index === undefined && b.index === undefined ? 0 : (a.index === undefined ? -1 : b.index === undefined ? 1 : (a.index > b.index ? 1 : (a.index == b.index ? 0 : -1)))
                    });
                    items.each(function (i) {
                        if (i.seperated && !i.functionality) {
                            menu.addSeparator().offer = i;
                            return
                        }
                        var menuItem = {handler: i.functionality, text: i.name, icon: i.icon, tooltip: i.description, checked: i.checked, offer: i};
                        if (i.iconCls) {
                            menuItem.iconCls = i.iconCls;
                            menuItem.activeClass = i.activeClass
                        }
                        menu.addMenuItem(menuItem);
                        if (i.seperated) {
                            menu.addSeparator()
                        }
                    });
                    if (offer.seperated) {
                        this.parent.addSeparator()
                    }
                    if (offer.onAdd instanceof Function) {
                        offer.onAdd(this.parent.items.last())
                    }
                } else {
                    if (offer.seperated && !offer.functionality) {
                        this.parent.addSeparator().offer = offer
                    } else {
                        var button = new Ext.Toolbar.Button({handler: offer.functionality, iconCls: "class_bugfixing", icon: offer.icon, disabled: true, toggleGroup: offer.toggleGroup, enableToggle: !!offer.toggleGroup, allowDepress: !offer.toggleGroup, pressed: offer.pressed, text: offer.name, tooltip: offer.description, offer: offer, cls: cls + " x-btn-text-icon"});
                        this.parent.add(button);
                        offer.button = button;
                        if (offer.onAdd instanceof Function) {
                            offer.onAdd(button)
                        }
                        if (offer.seperated) {
                            this.parent.addSeparator()
                        }
                    }
                }
            }.bind(this))
        }.bind(this));
        this.parent.render();
        menus.values().each(function (menu) {
            menu.render()
        });
        this.checkButtonState();
        this.updateResize()
    }, update: function () {
        this.checkButtonState(this.currentStores)
    }, checkButtonState: function (stores) {
        window.clearTimeout(this.timer);
        var me = this;
        var update = function (el, forceDisable) {
            if (!el || !el.offer) {
                return
            }
            var disabled = el.offer.isDisabled instanceof Function && el.offer.isDisabled();
            disabled = disabled || (el.offer.neededPrivileges && (!(stores instanceof Array) || !stores.all(function (store) {
                return store.hasPrivileges(el.offer.neededPrivileges)
            })));
            if (el.el && el.setDisabled) {
                el.el[disabled ? "addClass" : "removeClass"]("x-disabled");
                el.setDisabled(disabled)
            }
            el.setVisible(!(el.offer.isVisible instanceof Function) || el.offer.isVisible());
            if (el.menu && el.menu instanceof Ext.menu.Menu) {
                var allChildHidden = el.menu.items.items.all(function (item) {
                    return item.hidden
                });
                if (allChildHidden || el.menu.items.items.length <= 0) {
                    el.setDisabled(true)
                }
            }
        };
        this.timer = window.setTimeout(function () {
            var buttons = this.parent.items.items.findAll(function (b) {
                return(b instanceof Ext.Toolbar.Button || b.ctype == "Ext.Component" || b instanceof Ext.Toolbar.Separator) && b.offer
            });
            buttons.each(function (button) {
                update(button)
            });
            buttons = this.parent.items.items;
            buttons.each(function (button) {
                if (button instanceof Ext.Toolbar.Separator && !button.hidden) {
                    for (var i = buttons.indexOf(button) - 1; i >= 0; --i) {
                        if (buttons[i] instanceof Ext.Toolbar.Separator && !buttons[i].hidden) {
                            button.setVisible(false);
                            break
                        }
                        if (!buttons[i].hidden) {
                            break
                        }
                    }
                }
            });
            var menus = this.parent.items.items.findAll(function (b) {
                return(b.menu instanceof Ext.menu.Menu) && b.offer
            });
            menus.each(function (menu) {
                menu.menu.items.each(function (item) {
                    if (!item || !(item instanceof Ext.menu.Item)) {
                        return
                    }
                    update(item)
                });
                menu.menu.items.each(function (item, index) {
                    if (!item || !(item instanceof Ext.menu.Separator)) {
                        return
                    }
                    for (var i = index + 1; i < menu.menu.items.items.length; i++) {
                        if (!menu.menu.items.items[i].hidden) {
                            item.setVisible(true);
                            return
                        }
                    }
                    item.setVisible(false)
                });
                update(menu)
            })
        }.bind(this), 10)
    }};
    Signavio.Plugins.Toolbar = Signavio.Core.ContextPlugin.extend(Signavio.Plugins.Toolbar)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Plugins) {
    Signavio.Plugins = {}
}
new function () {
    Signavio.Plugins.Message = {enabled: true, construct: function (a) {
        arguments.callee.$.construct.apply(this, arguments)
    }, render: function () {
        this.tpl = new Ext.XTemplate("<div>{message}</div>");
        this.panel = new Ext.Panel({width: 400, cls: "x-message", renderTo: Ext.getBody()});
        this.panel.el.hide();
        this.syncPos();
        Signavio.Log.register(this.onLog.bind(this));
        Ext.getBody().on("resize", this.syncPos.bind(this))
    }, syncPos: function () {
        this.panel.el.center();
        this.panel.el.setTop(this.facade.getExtView("top").el.getHeight())
    }, onLog: function (e, d) {
        d = typeof d == "string" ? {message: d} : d;
        var a = Signavio.Core.graft("http://www.w3.org/1999/xhtml", this.panel.body, ["div", {"class": "x-message-item"}, d.message]);
        a = Ext.get(a).boxWrap();
        a.slideIn("t", {duration: 0.25});
        var b = window.setTimeout(function () {
            a.slideOut("t", {duration: 0.25, remove: true})
        }, Signavio.Config.WARNING_SHOWN || 3000);
        a.on("mouseover", function () {
            window.clearTimeout(b)
        });
        a.on("mouseout", function () {
            a.slideOut("t", {duration: 0.25, remove: true})
        })
    }};
    Signavio.Plugins.Message = Signavio.Core.ComponentPlugin.extend(Signavio.Plugins.Message)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Plugins) {
    Signavio.Plugins = {}
}
var abStore;
new function () {
    Signavio.Plugins.Breadcrumb = {enabled: true, viewGroup: ["icon", "table"], lastUrl: "", render: function () {
        this.template = new Ext.XTemplate('<span class="details">', '<tpl for=".">', "{[values.delimiter || Signavio.I18N.Repository.BreadCrumb.delimiter]}", '<a rel="{href}" onclick="{fn}" class="{[values.disabled?"x-disabled":""]}"> {name}</a>  ', "</tpl>", "</span>");
        this.panel = new Ext.Panel({cls: "breadcrumb_plugin", border: false, height: 30, html: "<span class='x-breadcrumb-wrapper'><span><span class='details'>" + Signavio.I18N.Repository.BreadCrumb.delimiter + "<span class='x-disabled'>" + Signavio.I18N.Repository.BreadCrumb.none + "</span></span></span><img class='x-msg-loading' style='display:none;' src='" + Signavio.Config.LIBS_PATH + "/ext-2.0.2/resources/images/default/tree/s.gif'/></span>"});
        var a = Signavio.Core.StoreManager.getChildEntitiesStore();
        a.on("beforeload", this.show.bind(this));
        a.on("load", this.update.bind(this));
        Ext.EventManager.onWindowResize(this.update.bind(this, false));
        var b = Signavio.Core.StoreManager.getRootDirectoryStore();
        b.on("beforeload", this.show.bind(this));
        b.on("load", this.hide.bind(this));
        Signavio.Core.Stores.RequestManager.on(Signavio.Core.Stores.RequestManager.BEFORELOAD, this.show.bind(this));
        Signavio.Core.Stores.RequestManager.on(Signavio.Core.Stores.RequestManager.FINISHED, this.hide.bind(this));
        this.facade.registerOnEvent(Signavio.Const.EVENT.FOLDER_SELECTION_CHANGE, this.onFolderSelectionChange.bind(this))
    }, afterRender: function () {
        this.panel.content = this.panel.body.first().first();
        this.panel.waiting = this.panel.body.first().last();
        this.panel.waiting.setVisibilityMode(Ext.Element.DISPLAY)
    }, onFolderSelectionChange: function (b, a) {
        this.bread = a.map(function (d) {
            return{href: d.identifier, name: d.name, fn: "Signavio.Core.StoreManager.loadChildEntitiesStore( (event.target||event.srcElement).getAttribute('rel') )"}
        });
        this.cachedBread = []
    }, count: 0, hide: function () {
        if (!this.panel || !this.panel.waiting || (--this.count > 0)) {
            return
        }
        this.panel.waiting.hide({duration: 0.2});
        this.count = 0
    }, show: function () {
        if (!this.panel || !this.panel.waiting || ++this.count !== 1) {
            return
        }
        this.panel.waiting.setDisplayed(true)
    }, update: function (a) {
        if (!a) {
            a = Signavio.Core.StoreManager.getChildEntitiesStore()
        }
        if (!a || !a.lastOptions) {
            return
        }
        this.hide();
        if (this.panel.bottombar && this.panel.bottombar.parent()) {
            this.panel.bottombar.update("")
        } else {
            var b = this.parent.el.child(".view_plugin");
            if (b) {
                this.panel.bottombar = b.createChild({html: "", cls: "x-breadcrumb-bottom-bar"})
            }
        }
        if (a.lastOptions.url.endsWith("search") || this.isSearched) {
            this.updateSearch(a.lastOptions.params.q, a)
        } else {
            this.updateBread(a)
        }
    }, updateBread: function (h) {
        if (!this.panel || !this.panel.content) {
            return
        }
        var d = this.getBreadCrumb(h);
        this.lastUrl = (d.last() || {}).href || "";
        this.template.overwrite(this.panel.content, d);
        var a = 100;
        if (this.panel.body.first().getWidth() - 10 > this.parent.getInnerWidth() - a) {
            var f = d.clone();
            var j = 0;
            var g = {name: "...", disabled: true};
            for (var b = 0; b < d.length; b++) {
                var e = (b % 2) === 0 ? d.length - ((b + 2) / 2) : ((b - 1) / 2);
                var k = d[e];
                j += (this.panel.content.first().dom.childNodes[((d.indexOf(k) * 2) + 1)].offsetWidth || 0) + 20;
                if (j > this.parent.getInnerWidth() - a && b > 0) {
                    if (g) {
                        f[e] = g;
                        g = false
                    } else {
                        f[e] = undefined
                    }
                }
            }
            this.template.overwrite(this.panel.content, f.compact())
        }
    }, updateSearch: function (h, l) {
        var m, j;
        m = j = l.data.items.length;
        try {
            m = l.getRecords("search")[0].get("rep").totalNrOfResults
        } catch (f) {
        }
        var k = new Ext.XTemplate(Signavio.I18N.Repository.BreadCrumb.nrOfResults).apply({nr: Math.max(m, 0)});
        if (!this.cachedBread || this.cachedBread.length == 0) {
            this.cachedBread = this.bread
        }
        this.bread = [
            {name: h + " (" + k + ")", delimiter: "" + Signavio.I18N.Repository.BreadCrumb.search, fn: "Signavio.Core.StoreManager.getRelatedStore( '/search', 'child', ['dir', 'mod'], {force:true,params:" + Object.toJSON(l.lastOptions.params).gsub('"', "'") + "})"}
        ];
        if (this.lastUrl) {
            this.bread.last().delimiter = "&raquo; " + this.bread.last().delimiter;
            this.bread.unshift({href: this.lastUrl, delimiter: "&laquo; ", name: Signavio.I18N.Repository.BreadCrumb.goBack, fn: "Signavio.Core.StoreManager.loadChildEntitiesStore( (event.target||event.srcElement).getAttribute('rel') )"})
        }
        this.template.overwrite(this.panel.content, this.bread);
        if (m > j && l.lastOptions && l.lastOptions.params) {
            var g = Math.floor(l.lastOptions.params.offset / l.lastOptions.params.limit);
            var a = Math.floor((m - 1) / l.lastOptions.params.limit);
            var b = g != 0 ? "<a href='#' class='x-first'>|<big>&laquo;</big></a> <a href='#' class='x-previous'><big>&laquo;</big></a>" : "<span class='x-disabled'><span class='x-first'>|<big>&laquo;</big></span> <big>&laquo;</big></span>";
            var d = g != a ? "<a href='#' class='x-next'><big>&raquo;</big></a> <a href='#' class='x-last'><big>&raquo;</big>|</a>" : "<span class='x-disabled'><big>&raquo;</big> <span class='x-last'><big>&raquo;</big>|</span></span>";
            [this.panel.content, this.panel.bottombar].compact().each(function (n) {
                var e = n.createChild({html: String.format("{0} {1}/{2} {3}", b, g + 1, a + 1, d), tag: "span", cls: "x-bread-search-pages"});
                e.select("a").on("click", function (p) {
                    var s = Ext.get(p.target);
                    Event.stop(p);
                    var q = Signavio.Core.StoreManager.getStore("child");
                    if (q && q.isLoading) {
                        return
                    }
                    if (!s.is("a")) {
                        s = s.parent("a")
                    }
                    if (!s) {
                        return
                    }
                    var r = Object.clone(l.lastOptions.params);
                    if (s.hasClass("x-first")) {
                        r.offset = 0
                    } else {
                        if (s.hasClass("x-previous")) {
                            r.offset -= r.limit
                        } else {
                            if (s.hasClass("x-next")) {
                                r.offset += r.limit
                            } else {
                                if (s.hasClass("x-last")) {
                                    r.offset = a * r.limit
                                }
                            }
                        }
                    }
                    Signavio.Core.StoreManager.getRelatedStore("/search", "child", ["mod", "dir"], {force: true, params: r})
                })
            })
        }
    }, getBreadCrumb: function (a) {
        this.bread = [];
        var d = a.getRecords("info")[0];
        if (d) {
            this.bread.push({href: a.getIdentifier(), name: Signavio.I18N.Repository.Folder[d.get("rep").type] || d.get("rep").name || d.get("rep").title || a.getIdentifier(), fn: "Signavio.Core.StoreManager.loadChildEntitiesStore( (event.target||event.srcElement).getAttribute('rel') )"})
        }
        var b = a.getRecords("parents")[0];
        if (b) {
            b.get("rep").each(function (e) {
                this.bread.unshift({href: e.href, name: Signavio.I18N.Repository.Folder[e.rep.type] || e.rep.name || e.rep.title || e.href, fn: "Signavio.Core.StoreManager.loadChildEntitiesStore( (event.target||event.srcElement).getAttribute('rel') )"})
            }.bind(this))
        }
        return this.bread.clone()
    }, onClick: function (a) {
        Signavio.Core.StoreManager.loadChildEntitiesStore(a.getAttribute("rel"))
    }, onViewSelect: function () {
        this.update()
    }};
    Signavio.Plugins.Breadcrumb = Signavio.Core.ViewPlugin.extend(Signavio.Plugins.Breadcrumb)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Plugins) {
    Signavio.Plugins = {}
}
new function () {
    Signavio.Plugins.IconView = {enabled: true, viewGroup: "icon", finialize: function () {
        if (!Signavio.Const.FRAGMENTS_ENABLED) {
            return
        }
        var b = window.location.hash;
        if (b.include(Signavio.Config.MODEL_PATH) || b.include(Signavio.Config.TALKABOUT_PATH)) {
            b = window.location.hash.replace(/.*\/model\/?/g, "/model/").replace(/.*\/talkabout\/?/g, "/model/");
            var a = this.panel.getNodeIndexesForRecordId(b);
            this.panel.select(a.first())
        }
    }, render: function () {
        var a = '<div class="empty-directory"><div class="thumb"><img title="' + Signavio.I18N.Repository.IconView.none + '" src="' + Signavio.Config.EXPLORER_PATH + '/src/img/nuvola/64x64/filesystems/folder_grey_white.png"/></div><span>' + Signavio.I18N.Repository.IconView.none + "</span></div>";
        this.panel = new Ext.DataView({store: Signavio.Core.StoreManager.getChildEntitiesStore(), tpl: Signavio.Templates.View, cls: "view_plugin", multiSelect: true, overClass: "x-view-over", itemSelector: "div.thumb-wrap", emptyText: a, anchor: "0 -38", plugins: [new Ext.DataView.DragSelector({dragSafe: false})], prepareData: function (d) {
            var b = Object.clone(d);
            b.rep = Object.clone(d.rep);
            b.rep.shortName = Signavio.Helper.SplitIntoTwoLines((b.rep.name || "").unescapeHTML(), (90 * 2));
            if (!b.rep.picture) {
                if (b.rel === Signavio.Const.REL.MODEL) {
                    b.rep.picture = Signavio.Config.BACKEND_PATH + b.rep.revision + Signavio.Const.THUMBNAIL
                } else {
                    b.rep.picture = Signavio.Config.EXPLORER_PATH + "/src/img/nuvola/64x64/filesystems/folder_grey.png"
                }
            }
            return b
        }, onRemove: function (e, b, d) {
            var f = this.getNode(d);
            if (f) {
                Ext.get(f).hide({duration: 0.25, remove: true, callback: function () {
                    if (d >= 0) {
                        Ext.DataView.prototype.onRemove.apply(this, arguments)
                    }
                    this.refresh()
                }.bind(this)})
            }
        }, onAdd: function (e, b, d) {
            Ext.DataView.prototype.onAdd.apply(this, arguments);
            this.clearSelections();
            b.each(function (f) {
                var g = this.getNode(this.store.indexOf(f));
                if (g) {
                    this.select(g, true);
                    Ext.fly(g).show({duration: 0.25})
                }
            }.bind(this))
        }, onUpdate: function (k, d) {
            var b = this.getNodeIndexesForRecordId(d.get("href"));
            for (var g = 0; g < b.length; g++) {
                var e = b[g];
                if (e >= 0) {
                    var j = this.isSelected(e);
                    var f = this.all.elements[e];
                    var h = this.bufferRender([d], e)[0];
                    h.setAttribute("id", d.get("href"));
                    this.all.replaceElement(e, h, true);
                    if (j) {
                        this.selected.replaceElement(f, h);
                        this.all.item(e).addClass(this.selectedClass)
                    }
                    this.updateIndexes(e, e)
                }
            }
        }, getNodeIndexesForRecordId: function (f) {
            var b = [];
            var d = this.getNodes();
            for (var e = 0; e < d.length; e++) {
                if (d[e].id == f) {
                    b[b.length] = this.indexOf(d[e])
                }
            }
            return b
        }, onBeforeLoad: function () {
            this._oldSelection = this.getSelectedRecords().map(function (b) {
                return b.get("href")
            });
            Ext.DataView.prototype.onBeforeLoad.apply(this, arguments)
        }, refresh: function () {
            Ext.DataView.prototype.refresh.apply(this, arguments);
            if (this._oldSelection) {
                var d = this.store;
                var b = this._oldSelection.map(function (e) {
                    return d.find("href", e)
                }).findAll(function (e) {
                    return e >= 0
                });
                this.select(b)
            }
        }, getSelectedRecords: function () {
            var h = [], g = this.selected.elements;
            for (var f = 0, b = g.length; f < b; f++) {
                var d = g[f].attributes.getNamedItem("id").value;
                var e = this.store.find("href", d);
                if (e >= 0) {
                    h.push(this.store.getAt(e))
                }
            }
            return h
        }, listeners: {selectionchange: function () {
            if (this.selectiontimer) {
                window.clearTimeout(this.selectiontimer)
            }
            this.selectiontimer = window.setTimeout(this.onSelectionChange.bind(this), 10)
        }.bind(this), click: function (e, d, g, f) {
            if (this.selectiontimer) {
                window.clearTimeout(this.selectiontimer)
            }
            this.selectiontimer = window.setTimeout(this.onSelectionChange.bind(this), 10);
            f.stopEvent()
        }.bind(this), dblclick: this.onDblClick.bind(this), containerclick: function (d, b) {
            var e = b.getTarget("div.dataview-group-header", 2, true);
            if (e) {
                e.up("div").toggleClass("x-grid-group-collapsed")
            }
            return !d.plugins[0].isDragging()
        }}});
        this.facade.registerOnEvent(Signavio.Const.EVENT.FOLDER_SELECTION_CHANGE, function (d, b) {
            if (this.facade.getTrashRecord() && b.first().identifier == this.facade.getTrashRecord().get("href")) {
                this.panel.addClass("trash")
            } else {
                this.panel.removeClass("trash")
            }
        }.bind(this));
        this.facade.registerOnEvent(Signavio.Const.EVENT.SEARCH_ACTIVATED, function () {
            this.panel.removeClass("trash")
        }.bind(this))
    }, afterRender: function () {
        new Ext.KeyMap(document, {key: "a", ctrl: true, fn: function (b, d) {
            if (d.target.tagName === "INPUT" || d.target.tagName === "TEXTAREA") {
                return
            }
            this.selectAll();
            d.stopEvent()
        }, scope: this});
        var a = new Ext.KeyMap(document, [
            {key: 37, fn: function (b, f) {
                if (Ext.getBody().hasClass("x-body-masked")) {
                    return
                }
                if (f.target.tagName === "A") {
                    return
                }
                if (f.target.tagName === "INPUT" || f.target.tagName === "TEXTAREA") {
                    return
                }
                if (!(this.panel.getNodes() instanceof Array) || this.panel.getNodes().compact().length < 1) {
                    return
                }
                var d = this.panel.getSelectedIndexes().first() || 0;
                this.panel.select(Math.max(0, --d));
                this.panel.getSelectedNodes().first().scrollIntoView(this.panel.el);
                f.stopEvent()
            }.bind(this)},
            {key: 39, fn: function (b, g) {
                if (Ext.getBody().hasClass("x-body-masked")) {
                    return
                }
                if (g.target.tagName === "A") {
                    return
                }
                if (g.target.tagName === "INPUT" || g.target.tagName === "TEXTAREA") {
                    return
                }
                if (!(this.panel.getNodes() instanceof Array) || this.panel.getNodes().compact().length < 1) {
                    return
                }
                var f = this.panel.getNodes().length - 1;
                var d = this.panel.getSelectedIndexes().first();
                d = d === undefined ? f : d;
                this.panel.select(Math.min(f, ++d));
                this.panel.getSelectedNodes().first().scrollIntoView(this.panel.el);
                g.stopEvent()
            }.bind(this)},
            {key: 38, fn: function (b, g) {
                if (Ext.getBody().hasClass("x-body-masked")) {
                    return
                }
                if (g.target.tagName === "A") {
                    return
                }
                if (g.target.tagName === "INPUT" || g.target.tagName === "TEXTAREA") {
                    return
                }
                if (!(this.panel.getNodes() instanceof Array) || this.panel.getNodes().compact().length < 1) {
                    return
                }
                var d = this.panel.getSelectedIndexes().first() || 0;
                var f = Math.floor((this.panel.el.dom.scrollWidth + 5) / (this.panel.getNodes().first().offsetWidth + 5));
                this.panel.select(Math.max(d % f, d - f));
                this.panel.getSelectedNodes().first().scrollIntoView(this.panel.el);
                g.stopEvent()
            }.bind(this)},
            {key: 40, fn: function (b, h) {
                if (Ext.getBody().hasClass("x-body-masked")) {
                    return
                }
                if (h.target.tagName === "A") {
                    return
                }
                if (h.target.tagName === "INPUT" || h.target.tagName === "TEXTAREA") {
                    return
                }
                if (!(this.panel.getNodes() instanceof Array) || this.panel.getNodes().compact().length < 1) {
                    return
                }
                var g = this.panel.getNodes().length - 1;
                var d = this.panel.getSelectedIndexes().first() || 0;
                var f = Math.floor((this.panel.el.dom.scrollWidth + 5) / (this.panel.getNodes().first().offsetWidth + 5));
                this.panel.select(Math.min(g, d + f));
                this.panel.getSelectedNodes().first().scrollIntoView(this.panel.el);
                h.stopEvent()
            }.bind(this)},
            {key: 13, fn: function (b, d) {
                if (Ext.getBody().hasClass("x-body-masked")) {
                    return
                }
                if (d.target.tagName === "INPUT" || d.target.tagName === "TEXTAREA") {
                    return
                }
                if (this.panel.getSelectedIndexes().length !== 1) {
                    return
                }
                this.onDblClick(this.panel, this.panel.getSelectedIndexes().first(), this.panel.getSelectedNodes().first(), d);
                d.stopEvent()
            }.bind(this)}
        ])
    }, selectAll: function () {
        if (this.panel.getNodes().length < 1) {
            return
        }
        this.panel.select(this.panel.getNodes());
        this.panel.fireEvent("selectionchange", this.panel, this.panel.getSelectedNodes())
    }, onDblClick: function (b, m, g, p) {
        var d = this.facade.getCurrentRootFolder();
        var j = this.facade.getTrashRecord();
        if (d && d === j) {
            return
        }
        var k = g.attributes.getNamedItem("id").value;
        var m = b.store.find("href", k);
        if (m < 0) {
            return
        }
        var l = b.store.getAt(m);
        if (l.get("rel") === Signavio.Const.REL.DIRECTORY) {
            var a = l.get("href");
            var f = Signavio.Core.StoreManager.getStore(a);
            if (f && f.isLoading) {
                f.missedCallbacks.push(function (h, n, e) {
                    Signavio.Core.StoreManager.loadChildEntitiesStore(n.id)
                })
            } else {
                Signavio.Core.StoreManager.loadChildEntitiesStore(a)
            }
        } else {
            if (l.get("rel") === Signavio.Const.REL.MODEL) {
                if (!Ext.isIE) {
                    var q = Signavio.Config.EDITOR_HANDLER_URI + "?" + Object.toQueryString({id: l.get("href").split("/")[2]});
                    window.open(q)
                } else {
                    window.open(Signavio.Config.PUBLISHER_URI + "/" + l.get("href").split("/").last())
                }
            }
        }
        p.preventDefault()
    }, onSelectionChange: function () {
        if (!this.isViewSelected()) {
            return
        }
        var b = this.panel.getSelectedRecords();
        var a = this.getSelection();
        if (b.length == a.length && b.findAll(function (d) {
            return a.include(d)
        }).length == a.length) {
            return
        }
        this._selection = b;
        this.facade.raiseEvent("selectionchange", b, a)
    }, onViewSelect: function () {
        var b = this.getSelection();
        var a = [];
        this.panel.clearSelections(true);
        b.each(function (d) {
            var e = this.panel.store.indexOf(d);
            if (e >= 0) {
                this.panel.select(e, true, true);
                a.push(d)
            }
        }.bind(this));
        if (a.length !== b.length) {
            this.facade.raiseEvent("selectionchange", a, b)
        }
    }};
    Signavio.Plugins.IconView = Signavio.Core.ViewPlugin.extend(Signavio.Plugins.IconView)
}();
if (!Signavio) {
    var Signavio = {}
}
if (!Signavio.Plugins) {
    Signavio.Plugins = {}
}
var abStore;
new function () {
    Signavio.Plugins.Info = {renderToView: "hover", enabled: true, relTypes: ["info", "revision"], render: function () {
        this.panel = new a({view: this, plugins: [new Ext.DataView.LabelEditor({labelSelector: "span.x-editable", onSave: this.onValueChange.bind(this, true)}), new Ext.DataView.LabelEditor({labelSelector: "div.x-editable", onSave: this.onValueChange.bind(this, false), tag: "textarea", grow: false, listeners: {beforestartedit: function (b) {
            b.field.el.setHeight(this.parent.fullviewShown ? 200 : 30)
        }.bind(this)}})]})
    }, afterRender: function () {
        this.parent.on("showfullview", this.panel.showFullView.bind(this.panel));
        this.parent.on("hidefullview", this.panel.hideFullView.bind(this.panel));
        this.parent.tools.toggle.addClass("x-disabled");
        this.facade.registerOnEvent(Signavio.Const.EVENT.SHOW_INFO_IMG, function (f, d) {
            this.panel.img.showImg(d)
        }.bind(this));
        var b = Signavio.Core.StoreManager.getChildEntitiesStore();
        b.on("load", function () {
            if (this.getSelection().length > 0) {
                return
            }
            var d = this.facade.getCurrentRootFolder();
            var e = this.facade.getCurrentFolderStore();
            if (!!d && d.get("href") === e.getIdentifier()) {
                this.panel.updateRootFolder(d)
            } else {
                this.panel.updateSingleFolder(e, true)
            }
        }.bind(this));
        new Ext.KeyMap(document, {key: " ", fn: function (d, f) {
            if ((f.target && f.target.tagName && ["input", "textarea"].include(f.target.tagName.toLowerCase())) || Ext.getBody().hasClass("x-body-masked")) {
                return
            }
            f.stopEvent();
            if (this.parent.fullviewShown) {
                this.parent.hideFullView()
            } else {
                this.parent.showFullView()
            }
        }, scope: this});
        new Ext.KeyMap(document, {key: 27, fn: function (d, f) {
            if ((f.target && f.target.tagName && ["input", "textarea"].include(f.target.tagName.toLowerCase())) || Ext.getBody().hasClass("x-body-masked")) {
                return
            }
            this.parent.hideFullView()
        }, scope: this});
        new Ext.KeyMap(document, {key: Ext.EventObject.ESC, fn: function (d, f) {
            if ((f.target && f.target.tagName && ["input", "textarea"].include(f.target.tagName.toLowerCase())) || Ext.getBody().hasClass("x-body-masked")) {
                return
            }
            if (this.parent.fullviewShown) {
                this.parent.hideFullView()
            }
        }, scope: this})
    }, openEditor: function (b) {
        if (!b) {
            return
        }
        var d = Signavio.Config.EDITOR_PATH + "?" + Object.toQueryString({id: b.get("href").split("/")[2]});
        window.open(d)
    }, onValueChange: function (h, e, g, d) {
        if (h && !(g || "").trim()) {
            this.panel.body.child(".x-info-details .x-editable").update(d.escapeHTML());
            return false
        } else {
            if (!h && !(g || "").trim()) {
                this.panel.body.child(".details div.x-editable").update(Signavio.I18N.Repository.Info.Attributes.nodescription)
            }
        }
        if (g === d) {
            return
        }
        var f = this.currentRecordsSet.values();
        if (!Signavio.Config.USE_CACHE) {
            Signavio.Core.Stores.RequestManager.onNextLoading(function () {
                Signavio.Core.StoreManager.getChildEntitiesStore().reload()
            })
        }
        if (f.length === 0 && this.panel.folder) {
            var b = this.panel.folder;
            var j = Object.clone(b.get("rep"));
            j.name = (j.name || "").unescapeHTML();
            j.description = (j.description || "").unescapeHTML();
            j[e.boundEl.id.replace("info_", "")] = g;
            b.set("rep", j, true);
            b.commit()
        } else {
            if (f.length > 0) {
                f.each(function (m) {
                    var k = (m instanceof Array ? m : [m]).find(function (n) {
                        return n.get("rel") == "info"
                    });
                    if (k) {
                        var l = Object.clone(k.get("rep"));
                        l.name = (l.name || "").unescapeHTML();
                        l.description = (l.description || "").unescapeHTML();
                        l[e.boundEl.id.replace("info_", "")] = g;
                        k.set("rep", l);
                        k.commit()
                    }
                })
            }
        }
    }, update: function (e, m) {
        if (!this.parent || !this.panel.rendered) {
            return
        }
        var l = e.values().length <= 0;
        var f = {};
        var b = e.values().compact();
        var m = m.values().compact();
        if (this._oldRecords) {
            var q = this._oldRecords.keys();
            var p = e.keys();
            if (q.length !== p.length) {
                this.parent.hideFullView()
            } else {
                if (!q.all(function (r) {
                    return p.member(r)
                })) {
                    this.parent.hideFullView()
                }
            }
        } else {
            this.parent.hideFullView()
        }
        this._oldRecords = e;
        this.parent.tools.toggle.addClass("x-disabled");
        var d = this.facade.getCurrentRootFolder();
        var h = this.facade.getCurrentFolderStore();
        var j = this.facade.getTrashRecord();
        var k = h && h === j;
        var n = !!d && d.get("href") === h.getIdentifier();
        if (m.length === 1 && m[0] instanceof Signavio.Core.ModelStore) {
            var g = m[0].hasPrivileges(Signavio.Config.RIGHTS.WRITE);
            this.parent.tools.toggle.removeClass("x-disabled");
            this.panel.updateSingleModel(m[0], k || !g)
        } else {
            if (m.length === 1) {
                var g = m[0].hasPrivileges(Signavio.Config.RIGHTS.WRITE);
                this.panel.updateSingleFolder(m[0], k || !g)
            } else {
                if (m.length > 1) {
                    this.panel.updateMultipleElements(b)
                } else {
                    if (m.length === 0 && n) {
                        this.panel.updateRootFolder(d)
                    } else {
                        this.panel.updateSingleFolder(h, true)
                    }
                }
            }
        }
    }, getCurrentFolder: function () {
        return this.facade.getCurrentRootFolder() || this.facade.getCurrentFolderStore()
    }};
    Signavio.Plugins.Info = Signavio.Core.ContextPlugin.extend(Signavio.Plugins.Info);
    var a = function () {
        a.superclass.constructor.apply(this, arguments)
    };
    Ext.extend(a, Ext.Panel, {region: "center", border: false, cls: "x-info-box", showFullView: function () {
        if (!this.store || !this.img || this.view.parent.collapsed || this.view.parent.tools.toggle.hasClass("x-disabled")) {
            return false
        }
        this.img.showImg(Signavio.Config.BACKEND_PATH + this.store.getHeadRevision().get("href") + "/png")
    }, hideFullView: function () {
        if (!this.img) {
            return
        }
        this.img.hideImg()
    }, onRender: function () {
        a.superclass.onRender.apply(this, arguments);
        Signavio.Core.graft("http://www.w3.org/1999/xhtml", this.body.dom, ["div", {"class": "x-info-details"}]);
        Signavio.Core.graft("http://www.w3.org/1999/xhtml", this.body.dom, ["div", {"class": "x-info-model", style: "position:relative;width:100%;height:100%;overflow:auto;"}, ["img", {"class": "x-msg-loading", style: "display:none;", src: Signavio.Config.LIBS_PATH + "/ext-2.0.2/resources/images/default/tree/s.gif"}]]);
        this.details = this.body.first();
        this.img = this.body.last();
        this.img.setWidth("100%");
        this.img.dom.style.position = "relative";
        this.img.dom.style.overflow = "auto";
        var b = function (d) {
            if (d) {
                return d && d.dom.naturalWidth && d.dom.naturalHeight && d.dom.naturalWidth === d.dom.width && d.dom.width <= d.dom.parentNode.offsetWidth && d.dom.naturalHeight === d.dom.height && d.dom.height <= d.dom.parentNode.offsetHeight
            }
        };
        this.img.waiting = this.img.last();
        this.img.imgs = {};
        this.img.showImg = function (e) {
            if (!this.img.imgs[e]) {
                var d = Ext.get(Signavio.Core.graft("http://www.w3.org/1999/xhtml", this.img, ["img", {src: e, style: "display:none;background:white;"}]));
                this.img.waiting.show(true);
                this.img.loadingImg = d;
                d.on("load", function () {
                    this.img.hideImg();
                    d.show(true);
                    this.img.shownImg = d;
                    this.img[b(this.img.shownImg) ? "removeClass" : "addClass"]("x-scrollenable");
                    this.img.waiting.hide({useDisplay: true});
                    delete this.img.loadingImg
                }.bind(this));
                d.setVisibilityMode(Ext.Element.DISPLAY);
                this.img.imgs[e] = d
            } else {
                if (this.img.shownImg === this.img.imgs[e] || this.img.loadingImg === this.img.imgs[e]) {
                    return
                } else {
                    this.img.hideImg();
                    this.img.imgs[e].show(true);
                    this.img.shownImg = this.img.imgs[e];
                    this.img[b(this.img.shownImg) ? "removeClass" : "addClass"]("x-scrollenable")
                }
            }
        }.bind(this);
        this.img.hideImg = function () {
            if (this.img.shownImg) {
                this.img.shownImg.hide(true);
                delete this.img.shownImg
            }
        }.bind(this);
        this.on("resize", function () {
            this.img.setHeight(this.body.getHeight() - this.details.getHeight() - 4);
            this.img[b(this.img.shownImg) ? "removeClass" : "addClass"]("x-scrollenable")
        }.bind(this));
        this.img.on("click", function () {
            if (this.img.hasClass("x-scrollenable")) {
                this.img.toggleClass("x-scrollable")
            }
        }.bind(this));
        this.resetView()
    }, updateSingleModel: function (d, j) {
        delete this.folder;
        this.store = d;
        var h = d.getRecords(Signavio.Const.REL.INFO)[0];
        h = Object.clone(h.get("rep"));
        var b = this.view.facade.getUserRecord(h.author);
        h.authorName = b ? b.get("rep").name.strip() || b.get("rep").mail.strip() || b.get("rep").principal.strip() || h.author : h.author;
        h.authorName = h.authorName === "/user/" ? "User" : h.authorName;
        h.bDate = this.beautifyDates(h.updated || h.created);
        h.notEditable = j === true;
        h.description = h.description || "";
        h.info = new Template(Signavio.I18N.Repository.Info.Attributes.info).evaluate({time: h.bDate, user: h.authorName});
        var e = d.getRecords(Signavio.Const.REL.NOTIFY)[0];
        h.showNotify = !this.view.facade.getCurrentUser().getRecords("info")[0].get("rep").isGuestUser && !Signavio.Config.HIDE_NOTIFICATION;
        h.notify = e.get("rep")["warehouse.model.save"];
        var g = Signavio.Templates.InfoModel.overwrite(this.details, h, true);
        var f = g.query("a.x-info-notify-link")[0];
        Ext.EventManager.addListener(f, "click", function () {
            var k = Object.clone(e.get("rep"));
            k["warehouse.model.save"] = !k["warehouse.model.save"];
            k["talkabout.notify.newcomment"] = k["warehouse.model.save"];
            e.set("rep", k);
            e.commit()
        }, this, {preventDefault: true})
    }, updateRootFolder: function (b) {
        var d = {name: Signavio.I18N.Repository.Folder[b.get("rep").type] || b.get("rep").name};
        Signavio.Templates.InfoRootFolder.overwrite(this.details, d)
    }, updateSingleFolder: function (b, f) {
        delete this.store;
        var e = b instanceof Ext.data.Record ? b : undefined;
        e = e || (b.snapshot || b.data).items.find(function (g) {
            return g.get("rel") == "info"
        });
        this.folder = e;
        var d = e ? Object.clone(e.get("rep")) : {};
        d.name = Signavio.I18N.Repository.Folder[d.type] || d.name;
        d.notEditableTitle = b instanceof Ext.data.Record;
        d.notEditable = f === true;
        d.description = d.description || "";
        if (Signavio.Templates.InfoFolder) {
            Signavio.Templates.InfoFolder.overwrite(this.details, d)
        }
    }, updateMultipleElements: function (e) {
        delete this.folder;
        delete this.store;
        var d = e.map(function (f) {
            f = f instanceof Array ? f : [f];
            return f.find(function (g) {
                return g.get("rel") === Signavio.Const.REL.INFO
            })
        }).compact();
        var b = this.beautifyDates(d.map(function (f) {
            return f.get("rep").updated || f.get("rep").created
        })).split(" + ");
        if (b.length > 1) {
            b = new Template(Signavio.I18N.Repository.Info.Attributes.infoMulipleTwo).evaluate({time: b[0], time2: b[1]})
        } else {
            b = new Template(Signavio.I18N.Repository.Info.Attributes.infoMulipleOne).evaluate({time: b[0]})
        }
        Signavio.Templates.InfoMultiple.overwrite(this.details, {count: e.length, updated: b})
    }, resetView: function () {
        if (Signavio.Templates.No) {
            Signavio.Templates.No.overwrite(this.details, {})
        }
    }, beautifyDates: function (f) {
        f = f instanceof Array ? f : [f];
        var b = function (j, h, g) {
            g = j == g ? null : g;
            return"<span title='" + (j && j instanceof Date ? j.format(Signavio.Const.DATE_FORMAT) : "") + (g && g instanceof Date ? " - " + g.format(Signavio.Const.DATE_FORMAT) : "") + "'>" + h + "</span>"
        };
        f = f.map(function (g) {
            return Signavio.Helper.ParseDate(g)
        }).sort(function (h, g) {
            return h.getTime() - g.getTime()
        }).reverse();
        var d = Signavio.Helper.BeautifyDate(f.last(), true);
        var e = Signavio.Helper.BeautifyDate(f.first(), true);
        return d != e ? b(f.last(), d) + " + " + b(f.first(), e) : b(f.first(), e, f.last())
    }})
}();
            if(!Signavio){ var Signavio = {} };
	if (!Signavio.Core) { Signavio.Core = {} };
	Signavio.Core.Version = "1.0.0";
			
            Signavio.Config.EXPLORER_PATH = '/editor/explorer';
            Signavio.Config.EDITOR_PATH = '/editor/editor';
            Signavio.Config.BACKEND_PATH = '/editor/p';
            Signavio.Config.LIBS_PATH = '/editor/libs';

            Signavio.Config.DIAGRAMS_IMAGE_PATH = Signavio.Config.EXPLORER_PATH + "/src/img"
            Signavio.Config.STENCILSET_EXTENSION_PATH = Signavio.Config.EDITOR_PATH + "/stencilsets/extensions/";
            Signavio.Config.PATH = Signavio.Config.EXPLORER_PATH + '/src/javascript';
            Signavio.Config.PLUGIN_PATH = Signavio.Config.EXPLORER_PATH + '/src/javascript/plugins';
            Signavio.Config.EDITOR_HANDLER_URI = Signavio.Config.BACKEND_PATH + "/editor";
            Signavio.Config.STENCILSET_URI = Signavio.Config.BACKEND_PATH + "/editor_stencilset";
            Signavio.Config.PLUGIN_CONFIG = Signavio.Config.BACKEND_PATH + "/explorer_plugins";
            Signavio.Config.USE_CACHE = false;
            Signavio.Config.REMOVE_ON_DELETE = true;
            Signavio.Config.HIDE_NOTIFICATION = true;
        