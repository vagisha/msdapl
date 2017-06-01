/**
 * AddInstrumentForm.java
 * @author Vagisha Sharma
 * Oct 28, 2009
 * @version 1.0
 */
package org.yeastrc.www.admin;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import java.util.regex.Pattern;

/**
 * 
 */
public class AddInstrumentForm extends ActionForm {

    private int id = 0;
    private String name;
    private String description;
    private boolean active;
    private String color;

    private static final Pattern HEX_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if(name == null || name.trim().length() == 0)
        {
            errors.add("instrument", new ActionMessage("error.instrument.noname"));
        }
        if(color == null || !HEX_PATTERN.matcher(color).matches())
        {
            errors.add("instrument", new ActionMessage("error.instrument.nocolor"));
        }
        return errors;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public String getColor()
    {
        return color;
    }

    public void setColor(String color)
    {
        this.color = color;
    }
}
