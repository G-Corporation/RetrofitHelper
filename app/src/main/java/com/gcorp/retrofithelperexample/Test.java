package com.gcorp.retrofithelperexample;

import android.util.JsonReader;
import com.google.gson.Gson;

import java.io.StringReader;

public class Test {

    void aa(){
        String json= "{items=[{name=Fruit Ninja Fight, url=https://www.farsroid.com/wp-content/uploads/Fruit-Ninja-Fight-Logo-150x150.png}, {name=Trailer Park Boys, url=https://www.farsroid.com/wp-content/uploads/Trailer-Park-Boys-Greasy-Money-logo-c-150x150.png}, {name=Cradle of Empires, url=https://www.farsroid.com/wp-content/uploads/Cradle-of-Empires-logo-d-150x150.png}, {name=Disco Ducks, url=https://www.farsroid.com/wp-content/uploads/Disco-Ducks-150x150.png}, {name=Manor Cafe, url=https://www.farsroid.com/wp-content/uploads/Manor-Cafe-logo-f-150x150.png}, {name=Live or Die: Survival, url=https://www.farsroid.com/wp-content/uploads/Live-or-Die-survival-2019-logo-150x150.jpg}]}";
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        T3 userinfo1 = (T3) gson.fromJson(String.valueOf(reader), T3.class);
    }
}
