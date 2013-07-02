package com.cellaflora.muni;

public class Person 
{
	public String name;
	public String title;
	public String group_a;
	public String group_b;
	public String email;
	public String tel_number;
	public String notes;
	
	public Person(String name)
	{
		this.name = name;
		this.title = " ";
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public void setGroupA(String group_a)
	{
		this.group_a = group_a;
	}
	
	public void setGroupB(String group_b)
	{
		this.group_b = group_b;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public void setTelNumber(String tel_number)
	{
		this.tel_number = tel_number;
	}
	
	public void setNotes(String notes)
	{
		this.notes = notes;
	}
	
	public String toString()
	{
		return name;
	}

}
