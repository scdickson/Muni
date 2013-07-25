package com.cellaflora.muni;

import java.io.Serializable;

public class Person implements Serializable
{
    public String objectId;
	public String name;
	public String title;
	public String group_a;
	public String group_b;
	public String email;
	public String tel_number;
	public String notes;
    public String url;

    public Person(){}


	public String toString()
	{
		return name + "," + title + "," + group_a + "," + group_b + "," + email + "," + tel_number + "," + notes + "\n";
	}

}
