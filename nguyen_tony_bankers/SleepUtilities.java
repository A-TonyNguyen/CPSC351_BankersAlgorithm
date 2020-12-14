// Filename: SleepUtilities.java
// Description: This is a utility for causing a thread to sleep.
// We should be handling interrupted exceptions, but are not doing so for clarity.
//
// Author:  Tony Nguyen
// Date:    December 10, 2020

public class SleepUtilities
{
    public static void nap() throws InterruptedException
    {
        nap(NAP_TIME); // sleep between zero and NAP_TIME
    }

    public static void nap(int duration) throws InterruptedException
    {
        int sleeptime = (int) (NAP_TIME * Math.random());
        try
        {   
            Thread.sleep(sleeptime * 1000);
        }
        catch (InterruptedException e)
        {
            throw e;
        }
    }

    private static final int NAP_TIME = 5;
}
