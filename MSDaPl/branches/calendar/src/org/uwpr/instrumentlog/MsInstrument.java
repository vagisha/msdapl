package org.uwpr.instrumentlog;

public class MsInstrument {

	private int id;
	private String name;
	private String description;
	private boolean active;
	private String color;

	public MsInstrument(){}

	public MsInstrument(int id, String name, String description, boolean active) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.active = active;
	}

	public MsInstrument(int id, String name, String description, boolean active, String color) {
		this(id, name, description, active);
		this.color = color;
	}

	public int getID() {
		return id;
	}

	public String getName() {
		if(active)
			return name;
		else
		{
			return name + " (retired)";
		}
	}

	public String getNameOnly() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public boolean isActive() {
		return active;
	}

	public String getColor()
	{
		return color;
	}

	public String getHexColor()
	{
		if(color != null && !color.startsWith("#"))
		{
			return "#" + color;
		}
		return "#" + InstrumentColors.getColor(getID());
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public void setColor(String color)
	{
		this.color = color;
	}
}
