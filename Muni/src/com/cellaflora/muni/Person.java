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

    public Person(){}


	public String toString()
	{
		return name + "," + title + "," + group_a + "," + group_b + "," + email + "," + tel_number + "," + notes + "\n";
	}

}
