package com.example.lib;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FoodModel {
    HashMap<Calendar, Day> Days = new HashMap();


    protected class Food{
        String foodType;
        DateFormat time;
        int duration;
        int ounces;

        Food(){
            duration = 0;
            ounces = 0;
            time = DateFormat.getTimeInstance(Calendar.HOUR_OF_DAY);
        }
    }

    protected class Breastfeed extends Food{
        Breastfeed(){
            foodType = "BFD";
        }

        Breastfeed(int dur)
        {
            foodType = "BFD";
            duration =  dur;
        }
    }

    protected class Formula extends Food{
        Formula(){
            foodType = "FRM";
        }
        Formula(int oz){
            foodType = "FRM";
            ounces = oz;
        }

    }

    protected class Bottle extends Food{
        Bottle()
        {
            foodType = "BOT";
        }
        Bottle(int oz){
            foodType = "BOT";
            ounces = oz;
        }
    }

    protected class Pumped extends Food{
        int ouncesStored;

        Pumped(){
            foodType = "PMP";
        }

    }

    protected class Day{
        Calendar currDay;
        List<Food> Feedings;
        List<Food> Stored;

        Day(){
            currDay = Calendar.getInstance();
        }
    }

    protected class FreezerInfo{
        List<Food> FrozenStash = new LinkedList<Food>();
        boolean useOldest;

        FreezerInfo(){
            useOldest = true;
        }

        public void useFrozen(int storageIndex, int numOunces){
            if(!FrozenStash.isEmpty())
            {
                if(FrozenStash.get(storageIndex).ounces - numOunces >= 0) {
                    FrozenStash.get(storageIndex).ounces -= numOunces;
                }
                else
                {
                    // Prompt that there is not enough milk from current source
                    // Optionally continue to use portion of next "bag"
                }

            }
            else
            {
                // Prompt that there is no milk in frozen storage
            }
        }

    }

}
