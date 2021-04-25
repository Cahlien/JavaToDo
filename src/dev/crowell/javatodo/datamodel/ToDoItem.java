package dev.crowell.javatodo.datamodel;

import java.time.LocalDate;

public class ToDoItem
{
    private String shortDescription;
    private String longDescription;
    private LocalDate deadline;

    public ToDoItem(String shortDescription, String longDescription, LocalDate deadline)
    {
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.deadline = deadline;
    }

    public String getShortDescription()
    {
        return shortDescription;
    }

    public String getLongDescription()
    {
        return longDescription;
    }

    public LocalDate getDeadline()
    {
        return deadline;
    }

    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    public void setLongDescription(String longDescription)
    {
        this.longDescription = longDescription;
    }

    public void setDeadline(LocalDate deadline)
    {
        this.deadline = deadline;
    }

    @Override
    public String toString()
    {
        return shortDescription;
    }
}
