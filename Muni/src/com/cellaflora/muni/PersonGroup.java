package com.cellaflora.muni;

import java.util.ArrayList;

/**
 * Created by sdickson on 7/3/13.
 */
public class PersonGroup
{
    public String groupName;
    public ArrayList<Person> people;
    public ArrayList<PersonGroup> subGroup;

    public PersonGroup(String groupName)
    {
        this.groupName = groupName;
        subGroup = new ArrayList<PersonGroup>();
        people = new ArrayList<Person>();
    }

    public int numSubGroups() //Recursively calculates number of subgroups this group is a parent of
    {
        int count = 0;

        for(PersonGroup group: subGroup)
        {
            count++;
            count+=group.numSubGroups();
        }

        return count;
    }

    public int numPeople() //Recursively calculates number of people this group is a parent of
    {
        int count = 0;

        for(Person person : people)
        {
            count++;
        }

        for(PersonGroup group: subGroup)
        {
            count+=group.numPeople();
        }

        return count;
    }

    public String toString()
    {
        String groupData = "\n" + groupName + "--" + subGroup.size() + " subgroups:\n\t";
        for(PersonGroup group : subGroup)
        {
            groupData += group.groupName + "--" + group.people.size() + " people\n\t";
        }
        return groupData;
    }
}
