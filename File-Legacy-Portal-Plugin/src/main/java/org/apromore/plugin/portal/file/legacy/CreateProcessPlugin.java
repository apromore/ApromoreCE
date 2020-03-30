package org.apromore.plugin.portal.file.legacy;

import java.util.Locale;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.dialogController.CreateProcessController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Messagebox;

public class CreateProcessPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(CreateProcessPlugin.class);

    private String label = "Create model (legacy editor)";
    private String groupLabel = "File";


    // PortalPlugin overrides

    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }

    @Override
    public String getIconPath() {
        return "bpmn-add.svg";
    }

    @Override
    public void execute(PortalContext portalContext) {
        MainController mainC = (MainController) portalContext.getMainController();

        mainC.eraseMessage();
        try {
            new CreateProcessController(mainC, mainC.getNativeTypes());
        } catch (SuspendNotAllowedException | InterruptedException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (ExceptionDomains e) {
            String message;
            if (e.getMessage() == null) {
                message = "Couldn't retrieve domains reference list.";
            } else {
                message = e.getMessage();
            }
            Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (ExceptionFormats e) {
            String message;
            if (e.getMessage() == null) {
                message = "Couldn't retrieve formats reference list.";
            } else {
                message = e.getMessage();
            }
            Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (ExceptionAllUsers e) {
            String message;
            if (e.getMessage() == null) {
                message = "Couldn't retrieve users reference list.";
            } else {
                message = e.getMessage();
            }
            Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
