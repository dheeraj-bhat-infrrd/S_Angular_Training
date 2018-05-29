
package com.realtech.socialsurvey.compute.entities.response.linkedin;

import java.io.Serializable;

/**
 * @author manish
 *
 */
public class LinkedinFeedServiceProvider implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "LinkedinFeedServiceProvider [name=" + name + "]";
    }
}
