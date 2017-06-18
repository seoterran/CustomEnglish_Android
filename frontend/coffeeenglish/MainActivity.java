/* Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*



 */
package com.frontend.coffeeenglish;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.englishsharing_1041.helloworld.Helloworld;
import com.appspot.englishsharing_1041.helloworld.model.HelloGreeting;
import com.appspot.englishsharing_1041.helloworld.model.HelloGreetingCollection;
import com.appspot.englishsharing_1041.helloworld.model.HelloQuestionCollection;
import com.appspot.englishsharing_1041.helloworld.model.HelloQuestionRequest;
import com.appspot.englishsharing_1041.helloworld.model.HelloUserCollection;
import com.appspot.englishsharing_1041.helloworld.model.HelloVerbCollection;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.Strings;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import static com.frontend.coffeeenglish.BuildConfig.DEBUG;

/**
 * Sample Android application for the Hello World tutorial for Google Cloud Endpoints. The sample
 * code shows many of the better practices described in the links below.
 *
 * @see <a href="https://developers.google.com/appengine/docs/java/endpoints">https://developers.google.com/appengine/docs/java/endpoints</a>
 * @see <a href="https://developers.google.com/appengine/docs/java/endpoints/consume_android">https://developers.google.com/appengine/docs/java/endpoints/consume_android</a>
 *
 *

 */
public class MainActivity extends Activity {
    private static final String LOG_TAG = "MainActivity";

  /**
   * Activity result indicating a return from the Google account selection intent.
   */
    DefaultHttpClient http_client = new DefaultHttpClient();
    private int m_state=0;
    private static int m_level=0;
    private int m_max_score=3;
    private float m_complete_rate=0;
    private DBManager dbManager;
    private  SQLiteDatabase db;
    private static final int ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION = 2222;
    private static int q_id=0;
    private static int pre_q_id=0;
    private static int m_answer;
    private int mScore_ID;
    private AuthorizationCheckTask mAuthTask;
    private String mEmailAccount = "";
    private GreetingsDataAdapter listAdapter;
    private Random m_rn = new Random();
    Button btn1,btn2,btn3;
    private int m_verb_size=200;
    private String [] m_verbs;
    private int rn=-1,prn=-1;
    private AutoCompleteTextView m_AutoCompleteText_verb;
    private MultiAutoCompleteTextView m_MultiAutoCompleteText_verbtohide;
    protected AccountManager accountManager;
    protected Intent intent;
    private  String m_gameID="",m_string="",m_hint="";
    private boolean m_dialog_result;
    private AlertDialog.Builder m_gg;
    private ProgressBar m_pro_bar;
    private List<Users> m_leaderboard = new ArrayList<Users>();
    private List<Verbs> m_verbStatus = new ArrayList<Verbs>();
    private List<HWs> m_hwStatus = new ArrayList<HWs>();
    private int m_hint_status=0;
    final private String m_str="?";
    private String m_dictionary_URL="http://www.yourdictionary.com/";;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.main, menu);
        Log.v(LOG_TAG, "onCreateOptionsMenu");
        return  super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item){
       // show_about(item.getItemId());

        m_gg=new AlertDialog.Builder(this);
        m_gg.setIcon(R.drawable.ic_launcher);
        switch (item.getItemId()) {
            case R.id.menu_about:
                View layout= View.inflate(MainActivity.this, R.layout.about, null);
                m_gg.setView(layout);
                final TextView tv=(TextView)layout.findViewById(R.id.about_tv);
                m_gg.setTitle("About " + getString(R.string.app_name) + " (Ver. "+ getString(R.string.version) +")");
                tv.setText(getString(R.string.about_message));
                dialog_show();
                break;
            case R.id.menu_leaderboard:
                m_gg.setTitle(getString(R.string.leaderboard)+" "+m_level);
                Log.v(LOG_TAG, " case R.id.menu_leaderboard:" + mEmailAccount + "," + m_gameID);
                get_leaderboard(mEmailAccount, m_gameID, m_level);
                break;
            case R.id.menu_status:
                //View.inflate(MainActivity.this, R.layout.dialog, null);
                //int hw=getIntfromSharedPreferences("key_homework");
                m_gg.setTitle("Level " + m_level );
                get_status();
                dialog_show();
                break;
            case R.id.menu_homework:
                int temp_hw=getNumHW();
                TextView temp_tv=(TextView)findViewById(R.id.textView2);
                temp_tv.setText(getString(R.string.HomeworkDescription) + " " + temp_hw);
               // set_visibility(View.VISIBLE);
                set_visibility_homework(View.VISIBLE);
                set_visibility_playing(View.GONE);
                break;

            case R.id.menu_hw_status:
                m_gg.setTitle("Assignment status");
                hw_status();
                dialog_show();
                break;
            case R.id.menu_dic:
                dictionary_show();
                break;
            case R.id.menu_feedback:
                feedback_show();
                break;
        }
        return true;
    }
    private int getRn(int n,String phrasalverb)
    {
        int temp;
        Log.v(LOG_TAG, "getRn: " + n);
        do{
            temp=m_rn.nextInt(n);
        }
        while((temp==rn)||(temp==prn)||m_verbs[temp].equals(phrasalverb));
        prn=rn;
        rn=temp;
        return rn;
    }


    private void set_visibility_playing(int visibility){

        findViewById(R.id.question).setVisibility(visibility);
        findViewById(R.id.button1).setVisibility(visibility);
        findViewById(R.id.button2).setVisibility(visibility);
        findViewById(R.id.button3).setVisibility(visibility);
        findViewById(R.id.answer).setVisibility(visibility);
        findViewById(R.id.webView).setVisibility(visibility);
        findViewById(R.id.webView).setVisibility(View.GONE);

    }
    private void set_visibility_homework(int visibility){
        findViewById(R.id.input_verb).setVisibility(visibility);

        EditText edit_verb= (EditText)findViewById(R.id.edit_verb);
        edit_verb.setText("");
        edit_verb.setVisibility(visibility);

        findViewById(R.id.input_sentence).setVisibility(visibility);

        EditText edit_sentence= (EditText)findViewById(R.id.edit_sentence);
        edit_sentence.setText(""); //"+sample_sentence);
        edit_sentence.setVisibility(visibility);

        findViewById(R.id.input_hint).setVisibility(visibility);

        EditText edit_hint= (EditText)findViewById(R.id.edit_hint);
        edit_hint.setText(""); //"+sample_sentence);
        edit_hint.setVisibility(visibility);

        findViewById(R.id.textView2).setVisibility(visibility);
        findViewById(R.id.submit).setVisibility(visibility);
        findViewById(R.id.cancel).setVisibility(visibility);

//        findViewById(R.id.assignment_status).setVisibility(View.GONE);
//        findViewById(R.id.hw_listview).setVisibility(View.GONE);

       // findViewById(R.id.assignment_status).setVisibility(visibility);


          // View layout= View.inflate(MainActivity.this, R.layout.hw_listview, null);
           // m_gg.setView(layout);
            //setContentView(layout);
           // ListView listview = (ListView) findViewById(R.id.hw_listview);
           /*
            if(listview!=null)
                listview.setAdapter(new HW_progAdapter());
            else
                Log.v(LOG_TAG, "no listview ");
            */
            //m_gg.show();

            //findViewById(R.id.hw_listview).setVisibility(visibility);
            //setContentView(R.layout.activity_main);
       // }


        //  findViewById(R.id.hw_listview).setVisibility(visibility);
    }

    public void onClickCancel (View v) {
        //set_visibility(View.GONE);
        set_visibility_homework(View.GONE);
        set_visibility_playing(View.VISIBLE);
    }
    public void dictionary_show(){

        final String []dictionary_list ={"Your Dictionary","Dictionary.com","Naver","Daum" };

        AlertDialog.Builder ad= new AlertDialog.Builder(this);
        ad.setSingleChoiceItems(dictionary_list, 0, null);
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                if (dictionary_list[selectedPosition].equals("Naver"))
                    m_dictionary_URL = "http://endic.naver.com/search.nhn?sLn=en&isOnlyViewEE=N&query=";
                else if (dictionary_list[selectedPosition].equals("Daum"))
                    m_dictionary_URL = "http://dic.daum.net/search.do?q=";
                else if (dictionary_list[selectedPosition].equals("Your Dictionary"))
                    m_dictionary_URL = "http://www.yourdictionary.com/";
                else if (dictionary_list[selectedPosition].equals("Dictionary.com"))
                    m_dictionary_URL = "http://dictionary.reference.com/browse/";

            }
        });
        ad.setNegativeButton("Cancel", null);
        ad.setTitle(getString(R.string.select_dictionary));
        ad.show();

    }
    public void parseForSearch(String str)
    {
        str=str.replaceAll("[!-+.^:;,_]","");
        str=str.replaceAll("/"," ");
        str=str.replaceAll("  "," ");
        str=str.replaceAll("  "," ");
        str=str.replaceAll("\\?","");
        final String []tokens = str.split(" ");
        Log.v(LOG_TAG, "onClickQuestion: " + str);
        ArrayList<String> word_list= new ArrayList<String >(tokens.length);
        for(int i=0;i<tokens.length;i++) {
            word_list.remove(tokens[i]);
            word_list.add(tokens[i]);
        }
        word_list.remove("Q");
        word_list.remove("A");

        final String[] tokens2 = word_list.toArray(new String[word_list.size()]);
        AlertDialog.Builder ad= new AlertDialog.Builder(this);
        ad.setSingleChoiceItems(tokens2, 0, null);
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                // Do something useful withe the position of the selected radio button

                WebView webView = (WebView) findViewById(R.id.webView);
                webView.setVisibility(View.VISIBLE);
                webView.setWebViewClient(new WebViewClient()); // 이걸 안해주면 새창이 뜸
                webView.loadUrl(m_dictionary_URL + tokens2[selectedPosition]);
            }
        });
        ad.setNegativeButton("Cancel", null);
        ad.setTitle(getString(R.string.select_word));
        ad.show();

    }
    public void onClickQuestion(View v) {
        String question=   ((Button)findViewById(R.id.question)).getText().toString();
        parseForSearch(question);
    }

    //submit homework
    public void onClickSubmit(View v){
        Log.v(LOG_TAG, "onClickSubmit m_verbs.length: " + m_verbs.length);
        Toast toast;
        EditText edit_sentence=(EditText)findViewById(R.id.edit_sentence);
        String sentence=edit_sentence.getText().toString();

        EditText edit_verb=(EditText)findViewById(R.id.edit_verb);
        String verb=edit_verb.getText().toString();

        EditText edit_hint=(EditText)findViewById(R.id.edit_hint);
        String hint=edit_hint.getText().toString();

        boolean contains=false;
        if((verb.isEmpty())||(sentence.isEmpty())||(hint.isEmpty()))
            return;

        /*
        String []tokens = verbtohide.split(" ");

        for(int i=0;i<tokens.length;i++)
            question=question.replaceAll(tokens[i],"___");

        Log.v(LOG_TAG, "question:" + question + "," + sentence);
        if(question.equals(sentence)){
            toast = Toast.makeText(MainActivity.this, "Please enter the words to hide again", Toast.LENGTH_SHORT );
            toast.show();
            return;
        }
        */
        for(int i=0;i<m_verbs.length;i++) {
            //Log.v(LOG_TAG, " m_verbs: "+m_verbs[i] );
            if(m_verbs[i].equals(verb)) {
                Log.v(LOG_TAG, " verb contained ");
                  contains = true;
                  break;
            }
        }
        if(!contains){
            Log.v(LOG_TAG, "no verb " );
            toast = Toast.makeText(MainActivity.this, getString(R.string.phrasalverbs), Toast.LENGTH_SHORT );
            toast.show();
            return;
        }

        Cursor score_cursor = db.rawQuery("select * from scores where verb='" + verb + "' ", null);
        score_cursor.moveToFirst();

        int verb_id=score_cursor.getInt(score_cursor.getColumnIndex("_id"));
        Log.v(LOG_TAG, "onClickSubmit verb,verb_id,hint:" + verb + "," + verb_id + hint);
        int id=getIntfromSharedPreferences("key_hw_ID");
        if(id==0)
            id=1;
        String sql= "INSERT INTO homeworks (_id, original,verb,hint,approved) VALUES( "+id+",'" +sentence+ "','"+verb +"','"+hint+"', 0)";
        try {
          //set_visibility(View.GONE);
          db.execSQL(sql);
         }catch (Exception e)
         {
          toast = Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT );
          toast.show();
          return;
         }

        send_question(sentence, verb_id, hint, id);

        id++;
        putIntToSharedPreferences("key_hw_ID", id);

        toast = Toast.makeText(MainActivity.this, getString(R.string.addsuccess), Toast.LENGTH_SHORT );
        toast.show();

        //set_visibility(View.GONE);
        set_visibility_homework(View.GONE);
        set_visibility_playing(View.VISIBLE);
    }

    // submit homework to server
    public void send_question(final String sentence,final long verb_id, final String hint,final long id){
        AsyncTask<Integer, Void, HelloGreeting> getAndDisplayGreeting =
                new AsyncTask<Integer, Void, HelloGreeting> () {
                    @Override
                    protected HelloGreeting doInBackground(Integer... integers) {
                        // Retrieve service handle using null credential since this is an unauthenticated call.
                        Helloworld apiServiceHandle = AppConstants.getApiServiceHandle(null);
                        Log.v(LOG_TAG,"send_question:"+sentence+","+hint);
                        try {
                            long temp=0;
                            Helloworld.Greetings.AddQuestion getGreetingCommand = apiServiceHandle.greetings().addQuestion("xxx",sentence, verb_id, hint, m_gameID,temp,id);
                            Log.v(LOG_TAG, "getGreetingCommand: "+getGreetingCommand);
                            HelloGreeting greeting=getGreetingCommand.execute();

                            return greeting;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);

                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(HelloGreeting greeting) {
                        if (greeting!=null) {
                            displayGreetings(greeting);

                        } else {
                            Log.e(LOG_TAG, "No greetings were returned by the API.");
                        }
                    }
                };

         getAndDisplayGreeting.execute(0);
    }
    public void onClickHint(View v) {
        Button hint_bt=(Button)findViewById(R.id.answer);
        String  str="";
        m_hint_status++;
        m_hint_status=m_hint_status%3;
        switch (m_hint_status)
        {
            case 0:
                str=m_str;
                break;
            case 1:
                str=m_hint;
                break;
            case 2:
                str=m_hint;
                parseForSearch(str);
                break;
        }

        hint_bt.setText(str);

    }

    public void onClickBtn(View v){
        boolean success=false;
        Log.v(LOG_TAG, "onclickbtn:" + m_answer + "," + v.getId());

        final TextView question_tv=(TextView)findViewById(R.id.question);
       int id=v.getId();
        ((Button)findViewById(id)).setBackgroundColor(Color.parseColor("#81c784"));//???
        ((Button)findViewById(id)).setTextSize(30);
        switch(id)
        {
            case R.id.button1:
                if(m_answer==1)
                    success = true;
                   // btn1.setBackgroundColor(Color.parseColor("#a5d6a7"));
                break;

            case R.id.button2:
                if(m_answer==2)
                    success=true;
                  //  btn2.setBackgroundColor(Color.parseColor("#a5d6a7"));
                break;

            case R.id.button3:
                if(m_answer==3)
                   success=true;
                  // btn3.setBackgroundColor(Color.parseColor("#a5d6a7"));
                break;
            default:
                success=false;
        }
        switch(m_answer){
            case 1:
                btn2.setText("");
                btn3.setText("");
                break;
            case 2:
                btn1.setText("");
                btn3.setText("");
                break;
            case 3:
                btn1.setText("");
                btn2.setText("");
                break;
        }

        Cursor score_cursor = db.rawQuery("select * from scores where _id="+mScore_ID+";", null);
        score_cursor.moveToFirst();
        String verb=score_cursor.getString(score_cursor.getColumnIndex("verb"));
        int trial=score_cursor.getInt(score_cursor.getColumnIndex("trial"));
        int score=score_cursor.getInt(score_cursor.getColumnIndex("score"));
        Log.v(LOG_TAG, "before Verb:" + verb + " trial:" + trial + " score:" + score);

        db.execSQL("update scores set trial = trial+1 where _id= " + mScore_ID + ";");
        Log.v(LOG_TAG, "execSQL 1");

      //  TextView textView_original = (TextView) findViewById(R.id.original);
        if( success)
        {
            Log.v(LOG_TAG, "success");
            db.execSQL("update scores set score = score +1 where _id=" + mScore_ID + ";");
            //correct.setText("You were correct!");
            //textView_original.setTextColor(Color.parseColor("#004d40"));
            question_tv.setTextColor(Color.parseColor(getString(R.string.textColor)));
        }
        else {
            Log.v(LOG_TAG, "not success");
            db.execSQL("update scores set score = score -2 where _id=" + mScore_ID + ";");
          //  correct.setText("You were wrong!");
           // textView_original.setTextColor(Color.parseColor("#FF0000"));
            question_tv.setTextColor(Color.parseColor("#FF0000"));
        }

        Log.v(LOG_TAG, "execSQL 2");
        score_cursor = db.rawQuery("select * from scores where _id=" + mScore_ID + ";", null);

        Log.v(LOG_TAG, "execSQL 3");
        score_cursor.moveToFirst();
        verb = score_cursor.getString(score_cursor.getColumnIndex("verb"));
         trial=score_cursor.getInt(score_cursor.getColumnIndex("trial"));
         score=score_cursor.getInt(score_cursor.getColumnIndex("score"));
        Log.v(LOG_TAG, "after Verb:" + verb + " trial:" + trial + " score:" + score);

        score_cursor = db.rawQuery("SELECT sum(score) FROM scores where  level='" + m_level + "';", null);
        score_cursor.moveToFirst();
        float sum=score_cursor.getInt(score_cursor.getColumnIndex("sum(score)"));
        m_complete_rate=  (sum/(m_verbs.length*m_max_score)) * 100;
        m_complete_rate=Math.min(100, m_complete_rate);
        m_complete_rate=Math.max(0, m_complete_rate);

        Log.v(LOG_TAG, "sum ,m_level->" + sum + "," + m_level);
        Log.v(LOG_TAG, "complete_rate=(sum/(m_verbs.length*m_max_score) )*100; ->" + m_complete_rate);

        Cursor cursor = db.rawQuery("select * from questions where _id=" + q_id + ";", null);
        cursor.moveToFirst();
        String original = cursor.getString(cursor.getColumnIndex("original"));
        question_tv.setText("A: " + original);

        btn1.animate();//???
        btn1.setClickable(false);
        btn2.setClickable(false);
        btn3.setClickable(false);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                question_tv.setTextColor(Color.parseColor("#ffffff"));
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn3.setVisibility(View.VISIBLE);
                btn1.setClickable(true);
                btn2.setClickable(true);
                btn3.setClickable(true);

                update_question();
            }
        }, 3000);
    }
    private int getNumHW(){

        Cursor c_hw=db.rawQuery("select * from homeworks where approved = 1" , null);
        int hw=c_hw.getCount();
        c_hw.close();
        return hw;

    }

    protected void update_question() {
        Log.v(LOG_TAG, "update_question");

        //set_visibility(View.GONE);
        set_visibility_homework(View.GONE);
        set_visibility_playing(View.VISIBLE);


        TextView accountTextView = (TextView) this.findViewById(R.id.gameID_tv);
        accountTextView.setText(m_gameID + " | L" + m_level);

        Log.v(LOG_TAG, "m_complete_rate: " + m_complete_rate);
        m_pro_bar.setProgress((int) m_complete_rate);
        m_pro_bar.setVisibility(View.VISIBLE);

        ((Button)findViewById(R.id.answer)).setText(m_str);

        m_hint_status=0;

        Button signin_bt=(Button)findViewById(R.id.sign_in_button);
        if(m_gameID.equals("xxx"))
            signin_bt.setVisibility(View.VISIBLE);
        else
            signin_bt.setVisibility(View.GONE);

        Cursor score_cursor, cursor;
        String sql, hint;
        if (q_id != 0)
            pre_q_id = q_id;
        if(m_level==0)
           m_level=1;

       // ((TextView)findViewById(R.id.level_tv)).setText("Level "+m_level);
        while (true) {
          Log.v(LOG_TAG, "While start");
          boolean default_case=false;
          try {
              int temp = m_rn.nextInt(4);
              switch (temp) {
                  case 0:
                  case 1:
                  case 2:
                      sql = "select * from scores  where trial=0  and level="+m_level+" ORDER BY RANDOM() LIMIT 1 ;";
                      Log.v(LOG_TAG, "random number is " + temp);
                      break;

                  default:
                      sql = "select * from scores  where  score < "+ m_max_score+" and  level="+m_level+" ORDER BY RANDOM() LIMIT 1 ;";
                      Log.v(LOG_TAG, "default case: random number is " + temp);
                      default_case=true;
                      break;
              }
              score_cursor = db.rawQuery(sql, null);
              Log.v(LOG_TAG, "update_question2" );
              if(score_cursor.getCount()==0) {
                  if(!default_case)
                      continue;
                  //int hw=getIntfromSharedPreferences("key_homework");

                  int hw=getNumHW();
                  if(hw==3) {
                      int level = getIntfromSharedPreferences("key_level");

                      String str = "Level " + level + " completed!";
                      Log.v(LOG_TAG, str);
                      Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
                      if (level < 4) {
                          level++;
                          m_level = level;
                          putIntToSharedPreferences("key_level", level);
                         // putIntToSharedPreferences("key_homework", 0);
                          String t_sql= "delete From homeworks;";
                          db.execSQL(t_sql);

                          continue;
                      } else {
                          Log.v(LOG_TAG, "Game Over! ");
                          score_cursor.close();
                          return;
                      }
                  }
                  else {
                          String str = getString(R.string.homework_warning);
                          Log.v(LOG_TAG, str);
                          Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

                  }
                  sql = "select * from scores where level='"+m_level+"' ORDER BY RANDOM() LIMIT 1 ;";
                  Log.v(LOG_TAG, "score_cursor.getCount()==0 " );
                  score_cursor = db.rawQuery(sql, null);
               }

              score_cursor.moveToFirst();
              int temp_score_id = score_cursor.getInt(score_cursor.getColumnIndex("_id"));
              Log.v(LOG_TAG, "temp_score_id:" + temp_score_id);

              cursor = db.rawQuery("select * from questions where verb= " + temp_score_id + " ORDER BY RANDOM() LIMIT 1;", null);
              cursor.moveToFirst();
              q_id = cursor.getInt(cursor.getColumnIndex("_id"));
              m_hint=cursor.getString(cursor.getColumnIndex("hint"));

          } catch (Exception e) {
              Log.v(LOG_TAG, "Couldn't dertermine q_id! m_level:"+m_level);
              continue;
          }
          break;
      }

      sql = "select * from questions where _id = " + q_id + ";";

      // Log.v(LOG_TAG, "db:" + db);
      cursor = db.rawQuery(sql, null);
      Log.v(LOG_TAG, "Query Result:" + cursor + "," + q_id);
      String example;
      if (cursor.moveToFirst()) {
          example = cursor.getString(cursor.getColumnIndex("question"));
          Log.v(LOG_TAG, "Query Result 2");

          mScore_ID = cursor.getInt(cursor.getColumnIndex("verb"));
          Log.v(LOG_TAG, " mScore_ID: " + mScore_ID);

        //  TextView textview_hint = (TextView) findViewById(R.id.answer);
         // textview_hint.setText(hint);
          m_answer = m_rn.nextInt(3) + 1;

      } else {
          Log.v(LOG_TAG, "move to first error 1");
          return;
      }

      score_cursor = db.rawQuery("select * from scores where _id=" + mScore_ID + ";", null);

      String phrasalverb;
      if (score_cursor.moveToFirst()) {
          phrasalverb = score_cursor.getString(score_cursor.getColumnIndex("verb"));
          Log.v(LOG_TAG, "id: " + q_id + ", question : " + example + ", answer : " + m_answer
                  + ", phrasalverb : " + phrasalverb);
      } else {
          Log.v(LOG_TAG, "move to first error 2");
          return;
      }

        Log.v(LOG_TAG, "Query Result 7");
        btn1.setBackgroundColor(Color.parseColor("#00000000"));
        btn2.setBackgroundColor(Color.parseColor("#00000000"));
        btn3.setBackgroundColor(Color.parseColor("#00000000"));
        btn1.setTextColor(Color.parseColor(getString(R.string.textColor)));
        btn2.setTextColor(Color.parseColor(getString(R.string.textColor)));
        btn3.setTextColor(Color.parseColor(getString(R.string.textColor)));
        btn1.setText(m_verbs[getRn(m_verbs.length, phrasalverb)]);
        btn2.setText( m_verbs[getRn(m_verbs.length,phrasalverb)]);
        btn3.setText(m_verbs[getRn(m_verbs.length, phrasalverb)]);


        Random rn=new Random();

        btn1.setTextSize(15 + rn.nextInt(10));
        btn2.setTextSize(15+rn.nextInt(10));
        btn3.setTextSize(15+rn.nextInt(10));
        switch (m_answer)
        {
              case 1:
                  btn1.setText(phrasalverb);
                  break;
              case 2:
                  btn2.setText(phrasalverb);
                  break;
              case 3:
                  btn3.setText(phrasalverb);
                  break;
        }

        TextView list=(TextView)findViewById(R.id.question);
        //TextView gameID_tv = (TextView) findViewById(R.id.gameID_tv);
        TextView completion_tv = (TextView) findViewById(R.id.mycompletion);
        String completion=String.format("%.01f", m_complete_rate);
       // gameID_tv.setText(m_gameID + " (" + completion + "%)");
        completion_tv.setText("       " + completion + "%");
        //sharedpreference http://developer.android.com/training/basics/data-storage/shared-preferences.html
        list.setTextColor((Color.parseColor(getString(R.string.textColor))));
        list.setText("Q: " + example);
       /*        if(pre_q_id!=0) {
           cursor = db.rawQuery("select * from questions where _id=" + pre_q_id + ";", null);
           cursor.moveToFirst();
           String original = cursor.getString(cursor.getColumnIndex("original"));
           TextView textView_original = (TextView) findViewById(R.id.original);
           textView_original.setText("A: " + original);
       }// list.setAdapter(Adapter);
        */
      //Log.v(LOG_TAG, "update_question3: "+cursor.getString(cursor.getColumnIndex("sample1")));
        Log.v(LOG_TAG, "update_question4");
        cursor.close();
        score_cursor.close();
        Log.v(LOG_TAG, "update_question5");
    }
    private String getStringfromSharedPreferences(String key)
    {
        SharedPreferences prefs = this.getSharedPreferences(
                "MyPref", Context.MODE_PRIVATE);
       return prefs.getString(key, "xxx");
    }
    private int getIntfromSharedPreferences(String key)
    {
        SharedPreferences prefs = this.getSharedPreferences(
            "MyPref", Context.MODE_PRIVATE);
        return prefs.getInt(key, 0);
    }

    private void  putIntToSharedPreferences(String key,int num)
    {
        SharedPreferences prefs = this.getSharedPreferences(
                "MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, num);
        editor.commit();

        if(getIntfromSharedPreferences(key)!=num)
            Log.v(LOG_TAG,"putIntToSharedPreferences failure");
    }
    private void  putStringToSharedPreferences(String key,String str)
    {
        SharedPreferences prefs = this.getSharedPreferences(
                "MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, str);
        editor.commit();

        if(!getStringfromSharedPreferences(key).equals(str))
            Log.v(LOG_TAG, "putStringToSharedPreferences failure");
    }
    public void toast(String str)
    {
        Toast.makeText(this, str , Toast.LENGTH_SHORT).show();
    }
    public void  dia(String message){
        View layout= View.inflate(MainActivity.this, R.layout.dialog, null);
        final EditText ed=(EditText)layout.findViewById(R.id.editID);
        ed.setTextColor(Color.parseColor(getString(R.string.textColor)));

        //final LinearLayout linear=(LinearLayout)View.inflate(MainActivity.this, R.layout.dialog, null);
        AlertDialog.Builder gg=new AlertDialog.Builder(this);
        gg.setView(layout);
        gg.setTitle(getString(R.string.app_name));
        gg.setMessage(message);
        gg.setIcon(R.drawable.ic_launcher);
        gg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                 //EditText editTitle=(EditText)linear.findViewById(R.id.editID);
                String temp = ed.getText().toString();
                if(temp.isEmpty()||(temp.length()>8))
                {
                    toast(getString(R.string.ID_warning1));
                    return;
                }
                String special = "!@#$%^&*()_.,";
                String pattern = ".*[" + Pattern.quote(special) + "].*";
                if (temp.matches(pattern)) //test required...
                {
                    toast(getString(R.string.ID_warning2)+"("+special+")");
                    return;
                }

                m_gameID=temp;
                //putStringToSharedPreferences("key_ID",m_gameID);

                onClickSignIn();
                m_dialog_result=true;
            }
        });
        gg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                //textView1.setText(msg);
                m_dialog_result = false;
            }
        });
        gg.show();
        return ;
    }
    private void hw_status(){

        Cursor hw_cursor = db.rawQuery("select * from homeworks", null);

        m_hwStatus.clear();
        try {
            if (hw_cursor.moveToFirst()) {
                do {
                    String verb= hw_cursor.getString(hw_cursor.getColumnIndex("verb"));
                    String original= hw_cursor.getString(hw_cursor.getColumnIndex("original"));
                    String hint= hw_cursor.getString(hw_cursor.getColumnIndex("hint"));
                    int status=  hw_cursor.getInt(hw_cursor.getColumnIndex("approved"));

                    HWs temp=new HWs(original,verb,hint,status);
                    m_hwStatus.add(temp);

                    Log.v(LOG_TAG, "original from homeworks: " + temp.original);
                    Log.v(LOG_TAG, "status from homeworks: " + temp.status);
                } while (hw_cursor.moveToNext());
            }
        }catch(RuntimeException e){
            Log.v(LOG_TAG,"FDa");
        }
        hw_cursor.close();


        View layout= View.inflate(MainActivity.this, R.layout.hw_list, null);
        m_gg.setView(layout);
        ListView listview = (ListView)  layout.findViewById(R.id.hw_listview);
        if(listview!=null)
            listview.setAdapter(new HW_progAdapter());
        else
            Log.v(LOG_TAG,"no listview");

        return ;
    }

    private void get_status() {
        Cursor score_cursor = db.rawQuery("select * from scores where level=" + m_level + " ORDER BY score desc;", null);
        int size=score_cursor.getCount();

        m_verbStatus.clear();
        try {
            if (score_cursor.moveToFirst()) {
                do {
                    String verb= score_cursor.getString(score_cursor.getColumnIndex("verb"));
                    int score= score_cursor.getInt(score_cursor.getColumnIndex("score"));
                    m_verbStatus.add(new Verbs(verb,score));
                } while (score_cursor.moveToNext());
            }
            }catch(RuntimeException e){
                    Log.v(LOG_TAG,"FDa");
            }
        score_cursor.close();

        View layout= View.inflate(MainActivity.this, R.layout.verb_list, null);
        m_gg.setView(layout);
        ListView   listview = (ListView)  layout.findViewById(R.id.verb_listview);
        if(listview!=null)
            listview.setAdapter(new verb_progAdapter((Application) this.getApplication()));

        return ;
    }

    public void get_leaderboard(final String email,final String name ,final long level) {

        // Use of an anonymous class is done for sample code simplicity. {@code AsyncTasks} should be
        // static-inner or top-level classes to prevent memory leak issues.
        // @see http://goo.gl/fN1fuE @26:00 for an great explanation.
        AsyncTask<Void, Void, HelloUserCollection> getAndDisplayGreeting =
                new AsyncTask<Void, Void, HelloUserCollection> () {
                    @Override
                    protected HelloUserCollection doInBackground(Void... unused) {
                        // Retrieve service handle using null credential since this is an unauthenticated call.
                        Helloworld apiServiceHandle = AppConstants.getApiServiceHandle(null);
                        Log.v(LOG_TAG, "email,name,level :"+ email+","+name+","+level);
                        try {
                            Helloworld.Greetings.GetLeaderboard getGreetingCommand = apiServiceHandle.greetings().getLeaderboard(email, name, level,(long)m_complete_rate);
                            HelloUserCollection greeting = getGreetingCommand.execute();
                            return greeting;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(HelloUserCollection greeting) {
                        if (greeting!=null && greeting.getItems()!=null) {
                            //displayQuestions(greeting.getItems().toArray(new HelloQuestionRequest[] {}));
                            parse_leaderboard(greeting);
                            //m_gg.setMessage(m_string);
                            dialog_show();
                        } else {
                            Log.e(LOG_TAG, "No greetings were returned by the API.");
                        }
                    }
                };
        getAndDisplayGreeting.execute((Void) null);
    }

    private void feedback_show(){
        //final LinearLayout linear=(LinearLayout)View.inflate(MainActivity.this, R.layout.feedback, null);
       // AlertDialog.Builder gg=new AlertDialog.Builder(this);
        View layout= View.inflate(MainActivity.this, R.layout.feedback, null);
        m_gg.setView(layout);
        final EditText ed=(EditText)layout.findViewById(R.id.editFeedback);
        ed.setTextColor(Color.parseColor(getString(R.string.textColor)));

        m_gg.setTitle("");
        m_gg.setMessage(getString(R.string.feedback));
        m_gg.setIcon(R.drawable.ic_launcher);

       m_gg.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int whichButton) {
               send_feedback(ed.getText().toString());
           }
       });
        m_gg.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                //send_feedback(editTitle.getText().toString());
            }
        });
        m_gg.show();
    }
    private void dialog_show() {
        m_gg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        m_gg.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG_TAG, " onCreate ");
       // set_visibility(View.GONE);

        setContentView(R.layout.activity_main);

        set_visibility_homework(View.GONE);
        set_visibility_playing(View.GONE);


        m_pro_bar=(ProgressBar) findViewById(R.id.progressBar);

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }

        btn1=(Button)findViewById(R.id.button1);
        btn2=(Button)findViewById(R.id.button2);
        btn3=(Button)findViewById(R.id.button3);

        try {

            dbManager = new DBManager(this);
            db = dbManager.getWritableDatabase();
            Log.v(LOG_TAG, "Database is there with version: " + dbManager.getReadableDatabase().getVersion());
        }
        catch (IOException e)
        {
            Log.v(LOG_TAG, "DBManager error");
        }
        //putIntToSharedPreferences("key_level", 2);
        int key_level=getIntfromSharedPreferences("key_level");
        if(key_level==0) {
            m_level=1;
            putIntToSharedPreferences("key_level", m_level);
        }
        else
            m_level= key_level;
        Cursor score_cursor = db.rawQuery("select verb from scores where level=" + m_level + ";", null);
        int size=score_cursor.getCount();

        m_verbs =new String[size];
        int i = 0;
        try {
            if (score_cursor.moveToFirst()) {
                do {
                    m_verbs[i] = score_cursor.getString(score_cursor.getColumnIndex("verb"));
                    // Log.v(LOG_TAG, "m_verbs[i]: " + m_verbs[i]);
                    i++;
                } while (score_cursor.moveToNext());
            }
        }catch (RuntimeException e) {
            Log.v(LOG_TAG, "move to first error 3");
        }
        Log.v(LOG_TAG, "m_verbs.length: "+m_verbs.length);
        m_AutoCompleteText_verb=(AutoCompleteTextView) findViewById(R.id.edit_verb);
        ArrayAdapter adapter =new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, m_verbs);
        m_AutoCompleteText_verb.setAdapter(adapter);

        // Prevent the keyboard from being visible upon startup.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


       // putStringToSharedPreferences("key_ID","aaaaaaa");
        m_gameID=getStringfromSharedPreferences("key_ID");
        m_dialog_result=true;
        if(m_gameID.equals("xxx"))
            dia("Please enter your ID");
        else
            sign_in(getStringfromSharedPreferences("key_email"));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("LOG_TAG", "onDestroy");
        putIntToSharedPreferences("key_completion", (int) m_complete_rate);
        db.close();
        dbManager.close();

        if (mAuthTask!=null) {
          mAuthTask.cancel(true);
          mAuthTask = null;
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
      //  Intent intent = getIntent();
       // AccountManager accountManager = AccountManager.get(getApplicationContext());
      //  Account account = (Account) intent.getExtras().get("account");
        //accountManager.getAuthToken(account, "ah", false, new GetAuthTokenCallback(), null);
    }
   private void sign_in(String email){
       m_state=1;
       mEmailAccount=email;
       putStringToSharedPreferences("key_ID", m_gameID);
       putStringToSharedPreferences("key_email",mEmailAccount);
       //m_gameID=getStringfromSharedPreferences("key_ID");
       //mEmailAccount = getStringfromSharedPreferences("key_email");


       if(m_complete_rate==0)
           m_complete_rate=getIntfromSharedPreferences("key_completion");

       send_user(mEmailAccount, m_gameID, m_level);
       get_questions(mEmailAccount, m_gameID, m_level);
       delete_questions(mEmailAccount, m_gameID, m_level);
       get_verbs(mEmailAccount, m_gameID, m_level);


       // m_level=getIntfromSharedPreferences("key_level");
       update_question();
}

    public void send_feedback(final String feedback){
        AsyncTask<Integer, Void, HelloGreeting> getAndDisplayGreeting =
                new AsyncTask<Integer, Void, HelloGreeting> () {
                    @Override
                    protected HelloGreeting doInBackground(Integer... integers) {
                        // Retrieve service handle using null credential since this is an unauthenticated call.
                        Helloworld apiServiceHandle = AppConstants.getApiServiceHandle(null);

                        try {
                            int completion=(int)m_complete_rate;
                            Helloworld.Greetings.AddFeedback getGreetingCommand = apiServiceHandle.greetings().addFeedback(m_gameID, feedback);
                            Log.v(LOG_TAG, "getGreetingCommand: "+getGreetingCommand);
                            HelloGreeting greeting=getGreetingCommand.execute();
                            return greeting;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(HelloGreeting greeting) {
                        if (greeting!=null) {
                            //displayGreetings(greeting);

                        } else {
                            Log.e(LOG_TAG, "No greetings were returned by the API.");
                        }
                    }
                };
        getAndDisplayGreeting.execute(0);
    }

    public void send_user(final String email,final String name ,final long level){
        AsyncTask<Integer, Void, HelloGreeting> getAndDisplayGreeting =
                new AsyncTask<Integer, Void, HelloGreeting> () {
                    @Override
                    protected HelloGreeting doInBackground(Integer... integers) {
                        // Retrieve service handle using null credential since this is an unauthenticated call.
                        Helloworld apiServiceHandle = AppConstants.getApiServiceHandle(null);

                        try {
                            putIntToSharedPreferences("key_completion", (int) m_complete_rate);
                            int completion=(int)m_complete_rate;
                            Log.v(LOG_TAG,"send_user:"+email+","+name);
                            Helloworld.Greetings.AddUser getGreetingCommand = apiServiceHandle.greetings().addUser(email, name, level, (long) m_complete_rate);
                            Log.v(LOG_TAG, "getGreetingCommand: "+getGreetingCommand);
                            HelloGreeting greeting=getGreetingCommand.execute();
                            return greeting;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(HelloGreeting greeting) {
                        if (greeting!=null) {
                           // displayGreetings(greeting);
                            Log.v(LOG_TAG, "greeting.getMessage():" + greeting.getMessage());
                            if( greeting.getMessage().equals("Failure!"))
                           {
                               Log.e(LOG_TAG, "sign in failure");
                               //Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                               dia("The Gmail account does not match your ID. Please try again");
                           }
                            else {
                                int temp=Integer.parseInt(greeting.getMessage());
                                Log.e(LOG_TAG, " level returned from server: " +temp );

                                putIntToSharedPreferences("key_level", temp);
                                m_level=temp;

                            }
                        } else {
                                    //Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                                    dia("The Gmail account does not match your ID. Please try again");
                        }
                    }
                };

        getAndDisplayGreeting.execute(0);
    }
    public void delete_questions(final String email,final String name ,final long level) {
        // Use of an anonymous class is done for sample code simplicity. {@code AsyncTasks} should be
        // static-inner or top-level classes to prevent memory leak issues.
        // @see http://goo.gl/fN1fuE @26:00 for an great explanation.
        AsyncTask<Void, Void, HelloQuestionCollection> getAndDisplayGreeting =
                new AsyncTask<Void, Void, HelloQuestionCollection> () {
                    @Override
                    protected HelloQuestionCollection doInBackground(Void... unused) {
                        // Retrieve service handle using null credential since this is an unauthenticated call.
                        Helloworld apiServiceHandle = AppConstants.getApiServiceHandle(null);
                        try {
                            int completion=(int)m_complete_rate;
                            Helloworld.Greetings.DeleteQuestions getGreetingCommand = apiServiceHandle.greetings().deleteQuestions(email, name, level, (long) m_complete_rate);
                            HelloQuestionCollection greeting = getGreetingCommand.execute();
                            return greeting;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(HelloQuestionCollection greeting) {
                        if (greeting!=null && greeting.getItems()!=null) {
                            //displayQuestions(greeting.getItems().toArray(new HelloQuestionRequest[] {}))
                            deleteQuestions(greeting);
                        } else {
                            Log.e(LOG_TAG, "No greetings were returned by the API.");
                        }
                    }
                };

        getAndDisplayGreeting.execute((Void)null);
    }

    //get new questions
    public void get_questions(final String email,final String name ,final long level) {

        // Use of an anonymous class is done for sample code simplicity. {@code AsyncTasks} should be
        // static-inner or top-level classes to prevent memory leak issues.
        // @see http://goo.gl/fN1fuE @26:00 for an great explanation.
        AsyncTask<Void, Void, HelloQuestionCollection> getAndDisplayGreeting =
                new AsyncTask<Void, Void, HelloQuestionCollection> () {
                    @Override
                    protected HelloQuestionCollection doInBackground(Void... unused) {
                        // Retrieve service handle using null credential since this is an unauthenticated call.
                        Helloworld apiServiceHandle = AppConstants.getApiServiceHandle(null);

                        try {
                            int completion=(int)m_complete_rate;
                            Helloworld.Greetings.GetQuestions getGreetingCommand = apiServiceHandle.greetings().getQuestions(email, name, level, (long) m_complete_rate);
                            HelloQuestionCollection greeting = getGreetingCommand.execute();
                            return greeting;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(HelloQuestionCollection greeting) {
                        if (greeting!=null && greeting.getItems()!=null) {
                            //displayQuestions(greeting.getItems().toArray(new HelloQuestionRequest[] {}));

                            saveQuestions(greeting);
                        } else {
                            Log.e(LOG_TAG, "No greetings were returned by the API.");
                        }
                    }
                };
         getAndDisplayGreeting.execute((Void)null);
    }
    public void get_verbs(final String email,final String name ,final long level) {

        // Use of an anonymous class is done for sample code simplicity. {@code AsyncTasks} should be
        // static-inner or top-level classes to prevent memory leak issues.
        // @see http://goo.gl/fN1fuE @26:00 for an great explanation.
        AsyncTask<Void, Void, HelloVerbCollection> getAndDisplayGreeting =
                new AsyncTask<Void, Void, HelloVerbCollection> () {
                    @Override
                    protected HelloVerbCollection doInBackground(Void... unused) {
                        // Retrieve service handle using null credential since this is an unauthenticated call.
                        Helloworld apiServiceHandle = AppConstants.getApiServiceHandle(null);
                        try {
                            int completion=(int)m_complete_rate;
                            Helloworld.Greetings.GetVerbs getGreetingCommand = apiServiceHandle.greetings().getVerbs(email, name, level, (long) m_complete_rate);
                            HelloVerbCollection greeting = getGreetingCommand.execute();
                            return greeting;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(HelloVerbCollection greeting) {
                        if (greeting!=null && greeting.getItems()!=null) {
                            //displayQuestions(greeting.getItems().toArray(new HelloQuestionRequest[] {}));

                            saveVerbs(greeting) ;
                        } else {
                            Log.e(LOG_TAG, "No greetings were returned by the API.");
                        }
                    }
                };

        getAndDisplayGreeting.execute((Void)null);
    }
    private void parse_leaderboard(HelloUserCollection users){
        int size=users.getItems().size();
        Log.v(LOG_TAG,"parse_leaderboard : ");
        int my_added_question=0;
        int i;String leaderboard="";
        m_leaderboard.clear();
        for(i=0;i<size;i++)
        {
            String t_name=users.getItems().get(i).getName();
            if(t_name.equals("xxx")) {
                break;
            }
            long t_completion=users.getItems().get(i).getCompletion();
            m_leaderboard.add(new Users(t_name, t_completion));
            Log.v(LOG_TAG, "m_leaderboard: " + m_leaderboard.get(i).name + "," + m_leaderboard.get(i).completion);
            leaderboard+=i+1+". "+ t_name + " (" +t_completion+"%)\n";
        }
        Log.v(LOG_TAG, "size: " + i);
        m_string=leaderboard;


    //    final LinearLayout linear=(LinearLayout)View.inflate(MainActivity.this, R.layout.leaderboard, null);
        //AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        //setContentView(R.layout.leaderboard);
       //
    //    final ListView listview= new ListView((getApplicationContext()));

        //Dialog dialog = new Dialog(this);
        View layout= View.inflate(MainActivity.this, R.layout.leaderboard, null);
        m_gg.setView(layout);

        ListView   listview = (ListView)  layout.findViewById(R.id.listview);
        if(listview!=null)
             listview.setAdapter(new progAdapter((Application)this.getApplication()));
        else
            Log.v(LOG_TAG, "no listview " );

    }
    private class Users{
       public String name;
        public  long completion;
        Users(String a,long b)
        {
            name=a;
            completion=b;
        }
    }
    private class Verbs{
        public String verb;
        public  int score;
        Verbs(String a,int b)
        {
            verb=a;
            score=b;
        }
    }
    private class HWs{
        public String original;
        public String verb;
        public String hint;
        public  int status;
        HWs(String a,String b,String c , int d)
        {
            original=a;
            verb=b;
            hint=c;
            status=d;
        }
    }
    private void saveVerbs(HelloVerbCollection verbs){
        int size = verbs.getItems().size();
        Log.v(LOG_TAG, "saveVerbs size: " + size);

        for (int i = 0; i < size; i++) {
            String verb = verbs.getItems().get(i).getVerb();
            if (verb.equals("xxx")) {
                Log.v(LOG_TAG, i + " verbs added");
                return;
            }
            Long id = verbs.getItems().get(i).getVerbId();
            Long level=verbs.getItems().get(i).getLevel();
            String sql= "INSERT INTO scores (_id, verb,trial,score,level) VALUES( " +id+ ",'"+verb+ "',0 ,0,"+ level +")";
            db.execSQL(sql);
            Log.v(LOG_TAG, "added phrasal verb: "+ id + ","+ verb+"," +level);
        }
    }
    private void deleteQuestions(HelloQuestionCollection questions) {
        int size = questions.getItems().size();
        Log.v(LOG_TAG, "deleteQuestions size: " + size);

        for (int i = 0; i < size; i++) {
            String original = questions.getItems().get(i).getOriginal();
            if (original.equals("xxx")) {
                Log.v(LOG_TAG, i + " questions deleted");
                return;
            }
            String sql= "delete From questions where original='"+original+ "';";
            db.execSQL(sql);
            Log.v(LOG_TAG, "deleted question: "+original);
        }
    }

    //add new questions received from server
    private void saveQuestions(HelloQuestionCollection questions)
    {
        int size=questions.getItems().size();
        Log.v(LOG_TAG,"saveQuestions size: "+size);
        int my_added_question=0;
        for(int i=0;i<size;i++)
        {
            String question=questions.getItems().get(i).getQuestion();
            if(question.equals("xxx")) {
                Log.v(LOG_TAG, i + " questions added");
                break;
            }

            String original=questions.getItems().get(i).getOriginal();
            long verb_id=questions.getItems().get(i).getVerb();
            String hint=questions.getItems().get(i).getHint();
            String author=questions.getItems().get(i).getAuthor();
            int status=(questions.getItems().get(i).getApproved()).intValue();
            int id= (questions.getItems().get(i).getId()).intValue();
            //Log.v(LOG_TAG, status + ": status has to be 1 here");

            //add new questions
            String sql= "INSERT INTO questions(_id, question,original,verb,hint,author) VALUES(null, '" +question+ "','"+original+ "',"+verb_id +",'"+hint+"','"+author+"')";
            db.execSQL(sql);

            Log.v(LOG_TAG, "added question: " + original+","+author+","+m_gameID);

            //if my homework, ubdate homeworks db
            if(author.equals(m_gameID)) {
                //update homeworks DB
                Log.v(LOG_TAG, "my question: " );
                db.execSQL("update homeworks set approved= "+status+ " where _id=" + id + ";");

               // if(status==1) //status 가 이미 1 인지 확인해야함
                 //   my_added_question++;
            }
        }

        if(my_added_question > 0) {
            String str = "Your " + my_added_question + " "+getString(R.string.question_added);
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
           // m_homework += my_added_question;
           // int hw=getIntfromSharedPreferences("key_homework");
            //putIntToSharedPreferences("key_homework", hw + my_added_question);

            //Log.v(LOG_TAG, "my_homework:" + getIntfromSharedPreferences("key_homework"));
        }
        else
            Log.v(LOG_TAG, "my_added_question ==0" );
    }

    class progAdapter extends ArrayAdapter {
        public progAdapter(Application application){
            super(application.getApplicationContext(), android.R.layout.simple_list_item_1,
                    m_leaderboard);
        }

        @Override
        public int getCount() {
            return m_leaderboard.size();
        }

        @Override
        public Object getItem(int position) {
            //Toast.makeText(getApplicationContext(), "getItem : " + position, Toast.LENGTH_SHORT).show();
            return m_leaderboard.get(position);
        }

        @Override
        public long getItemId(int position) {
            //Toast.makeText(getApplicationContext(), "getId : " + position, Toast.LENGTH_SHORT).show();
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //View view = (View)super.getView(position, convertView, parent);
            final Context context = parent.getContext();

            if(convertView == null){
                LayoutInflater li = LayoutInflater.from(context);
                convertView = li.inflate(R.layout.listview, parent, false);
            }


            TextView title = (TextView) convertView.findViewById(R.id.title);
            ProgressBar   pb = (ProgressBar) convertView.findViewById(R.id.prog);
            TextView  completion = (TextView) convertView.findViewById(R.id.completion);
            title.setText(m_leaderboard.get(position).name);
            pb.setProgress((int) m_leaderboard.get(position).completion);
            completion.setText( Long.toString( m_leaderboard.get(position).completion)+"%");
            return convertView;
        }
    }


    class verb_progAdapter extends ArrayAdapter {
        public verb_progAdapter(Application application){
            super(application.getApplicationContext(), android.R.layout.simple_list_item_1,
                    m_verbStatus);
        }

        @Override
        public int getCount() {
            return m_verbStatus.size();
        }

        @Override
        public Object getItem(int position) {
            //Toast.makeText(getApplicationContext(), "getItem : " + position, Toast.LENGTH_SHORT).show();
            return m_verbStatus.get(position);
        }

        @Override
        public long getItemId(int position) {
            //Toast.makeText(getApplicationContext(), "getId : " + position, Toast.LENGTH_SHORT).show();
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //View view = (View)super.getView(position, convertView, parent);
            Log.v(LOG_TAG, "verb_progAdapter.getView: "+position +","+convertView);
            final Context context = parent.getContext();

            if(convertView == null){
                LayoutInflater li = LayoutInflater.from(context);
                convertView = li.inflate(R.layout.verb_listview, parent, false);
                Log.v(LOG_TAG, "convertView == verb_listview ");
            }


            TextView verb = (TextView) convertView.findViewById(R.id.verb_tv);
            ProgressBar   pb = (ProgressBar) convertView.findViewById(R.id.verb_prog);
            //TextView  completion = (TextView) convertView.findViewById(R.id.completion);
            verb.setText(m_verbStatus.get(position).verb);

            int temp=m_verbStatus.get(position).score;
            if(temp<0) {
                pb.setMax(m_max_score - temp + 1);
                pb.setProgress(1);
            }
            else {
                pb.setMax(m_max_score);
                pb.setProgress(m_verbStatus.get(position).score);
            }
           // completion.setText( Long.toString( m_verbStatus.get(position).completion)+"%");
            return convertView;
        }
    }

    class HW_progAdapter extends ArrayAdapter {

        public HW_progAdapter(){
            super(getApplicationContext(), android.R.layout.simple_list_item_1,
                    m_hwStatus);
        }

        @Override
        public int getCount() {
            int size= m_hwStatus.size();
            Log.v(LOG_TAG, "HW_progAdapter.getCount: "+size );
            return size;
        }

        @Override
        public Object getItem(int position) {
            //Toast.makeText(getApplicationContext(), "getItem : " + position, Toast.LENGTH_SHORT).show();
            return m_hwStatus.get(position);
        }

        @Override
        public long getItemId(int position) {
            //Toast.makeText(getApplicationContext(), "getId : " + position, Toast.LENGTH_SHORT).show();
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //View view = (View)super.getView(position, convertView, parent);
            Log.v(LOG_TAG, "HW_progAdapter.getView: "+position +","+convertView);
            final Context context = parent.getContext();

            if(convertView == null){
               // Log.v(LOG_TAG, "convertView == null" );
                LayoutInflater li = LayoutInflater.from(context);
                convertView = li.inflate(R.layout.hw_listview, parent, false);
            }

            /*
            else{
                Log.v(LOG_TAG, "convertView != null" );
                LayoutInflater li = LayoutInflater.from(context);
                convertView = li.inflate(R.layout.hw_listview, parent, false);
                //return convertView;
            }*/
         //   Log.v(LOG_TAG, "HW_progAdapter.convertView: "+convertView);
            TextView sentence = (TextView) convertView.findViewById(R.id.hw_sentence);
            TextView  status = (TextView) convertView.findViewById(R.id.hw_status);
/*
            View view=super.getView(position,convertView,parent);

             sentence = (TextView) view.findViewById(R.id.hw_sentence);
              status = (TextView) view.findViewById(R.id.hw_status);
*/
            sentence.setText(m_hwStatus.get(position).original);
            Log.v(LOG_TAG,"original: "+m_hwStatus.get(position).original);
            String str="";
            switch(m_hwStatus.get(position).status)
            {
                case 0:
                    str="Under review";
                    break;
                case 1:
                    str="Pass";
                    break;
                case 2:
                    str="Fail";
                    break;
                default:
                    str="?";
                    break;
            }
           // Log.v(LOG_TAG, "m_hwStatus.get(position).status: " +str);
            status.setText("("+str+")");
            return convertView;
         //   return view;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

      if (requestCode == ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION && resultCode == RESULT_OK) {
          // This path indicates the account selection activity resulted in the user selecting a
          // Google account and clicking OK.

          // Set the selected account.

          TextView emailAccountTextView = (TextView)this.findViewById(R.id.gameID_tv);
          emailAccountTextView.setText(m_gameID);

          //    getUsername();
          // Fire off the authorization check for this account and OAuth2 scopes.
          mEmailAccount = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
          //performAuthCheck(mEmailAccount);
          sign_in(mEmailAccount);
          //putStringToSharedPreferences("key_email", mEmailAccount);
         // update_question();

      }
    }

    /**
     * Attempts to retrieve the username.
     * If the account is not yet known, invoke the picker. Once the account is known,
     * start an instance of the AsyncTask to get the auth token and do work with it.
     */
    /*
    private void getUsername() {
        if (mEmail == null) {
            pickUserAccount();
        } else {
            if (isDeviceOnline()) {
                new GetUsernameTask(HelloActivity.this, mEmail, SCOPE).execute();
            } else {
                Toast.makeText(this, R.string.not_online, Toast.LENGTH_LONG).show();
            }
        }
    }
    */
    private boolean isSignedIn() {
        if (!Strings.isNullOrEmpty(mEmailAccount)) {
          return true;
        } else {
          return false;
        }
    }


  /**
   * This method is invoked when the "Sign In" button is clicked. See activity_main.xml for the
   * dynamic reference to this method.
   */
  public void onClickSignUp(View view) {
     dia("Create your ID");
  }

  public void onClickSignIn(){
      //TextView emailAddressTV = (TextView) view.getRootView().findViewById(R.id.email_address_tv);
    // Check to see how many Google accounts are registered with the device.
    int googleAccounts = AppConstants.countGoogleAccounts(this);
    if (googleAccounts == 0) {
      // No accounts registered, nothing to do.
      Toast.makeText(this, R.string.toast_no_google_accounts_registered,
              Toast.LENGTH_LONG).show();
    } else if (googleAccounts == 1) {
      // If only one account then select it.
      Toast.makeText(this, R.string.toast_only_one_google_account_registered,
          Toast.LENGTH_LONG).show();
      AccountManager am = AccountManager.get(this);
      Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
      if (accounts != null && accounts.length > 0) {
        // Select account and perform authorization check.
         // emailAddressTV.setText(accounts[0].name);
          mEmailAccount = accounts[0].name;
         // performAuthCheck(accounts[0].name);
          sign_in(accounts[0].name);
          //putStringToSharedPreferences("key_email", mEmailAccount);
          Log.v(LOG_TAG, "onClickSignIn account ok " );
          //update_question();
      }
    } else {
      // More than one Google Account is present, a chooser is necessary.

      // Reset selected account.
      //  emailAddressTV.setText("");
      // Invoke an {@code Intent} to allow the user to select a Google account.
        Intent accountSelector = AccountPicker.newChooseAccountIntent(null, null,
              new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false,
                getString(R.string.select_account), null, null, null);
         startActivityForResult(accountSelector,
          ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION);
    }

  }
    private void displayGreetings(HelloGreeting... greetings) {
        String msg;
        if (greetings==null || greetings.length < 1) {
            msg = "Greeting was not present";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        } else {
            if (DEBUG) {
                Log.d(LOG_TAG, "Displaying " + greetings.length + " greetings.");
            }

            List<HelloGreeting> greetingsList = Arrays.asList(greetings);
///            listAdapter.replaceData(greetings);
        }
    }
  /**
   * Schedule the authorization check in an {@code Tasks}.
   */
  public void onClickGetAuthenticatedGreeting(View unused) {
      if (!isSignedIn()) {
          Toast.makeText(this, "You must sign in for this action.", Toast.LENGTH_LONG).show();
          return;
      }

      // Use of an anonymous class is done for sample code simplicity. {@code AsyncTasks} should be
      // static-inner or top-level classes to prevent memory leak issues.
      // @see http://goo.gl/fN1fuE @26:00 for an great explanation.
      AsyncTask<Void, Void, HelloGreeting> getAuthedGreetingAndDisplay =
              new AsyncTask<Void, Void, HelloGreeting> () {
                  @Override
                  protected HelloGreeting doInBackground(Void... unused) {
                      if (!isSignedIn()) {
                          return null;
                      };

                      if (!AppConstants.checkGooglePlayServicesAvailable(MainActivity.this)) {
                          return null;
                      }

                      // Create a Google credential since this is an authenticated request to the API.
                      GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                              MainActivity.this, AppConstants.AUDIENCE);
                      credential.setSelectedAccountName(mEmailAccount);

                      // Retrieve service handle using credential since this is an authenticated call.
                      Helloworld apiServiceHandle = AppConstants.getApiServiceHandle(credential);

                      try {
                          Helloworld.Greetings.Authed getAuthedGreetingCommand = apiServiceHandle.greetings().authed();
                          HelloGreeting greeting = getAuthedGreetingCommand.execute();
                          return greeting;
                      } catch (IOException e) {
                          Log.e(LOG_TAG, "Exception during API call", e);
                      }
                      return null;
                  }

                  @Override
                  protected void onPostExecute(HelloGreeting greeting) {
                      if (greeting!=null) {
                          displayGreetings(greeting);
                      } else {
                          Log.e(LOG_TAG, "No greetings were returned by the API.");
                      }
                  }
              };

      getAuthedGreetingAndDisplay.execute((Void)null);
  }
    private void displayQuestions(HelloQuestionRequest... greetings) {
        String msg;
        if (greetings==null || greetings.length < 1) {
            msg = "Greeting was not present";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        } else {
            if (DEBUG) {
                Log.d(LOG_TAG, "Displaying " + greetings.length + " greetings.");
            }

            List<HelloQuestionRequest> greetingsList = Arrays.asList(greetings);
            listAdapter.replaceData_question(greetings);
        }
    }

    public void performAuthCheck(String emailAccount) {
    // Cancel previously running tasks.
    if (mAuthTask != null) {
      try {
        mAuthTask.cancel(true);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return;
    }

    // Start task to check authorization.
    mAuthTask = new AuthorizationCheckTask();
    mAuthTask.execute(emailAccount);
  }

  /**
   * Verifies OAuth2 token access for the application and Google account combination with
   * the {@code AccountManager} and the Play Services installed application. If the appropriate
   * OAuth2 access hasn't been granted (to this application) then the task may fire an
   * {@code Intent} to request that the user approve such access. If the appropriate access does
   * exist then the button that will let the user proceed to the next activity is enabled.
   */
  class AuthorizationCheckTask extends AsyncTask<String, Integer, Boolean> {
    @Override
    protected Boolean doInBackground(String... emailAccounts) {
      Log.i(LOG_TAG, "Background task started.");

      if (!AppConstants.checkGooglePlayServicesAvailable(MainActivity.this)) {
        return false;
      }

      String emailAccount = emailAccounts[0];
      // Ensure only one task is running at a time.
      mAuthTask = this;

      // Ensure an email was selected.
      if (Strings.isNullOrEmpty(emailAccount)) {
        publishProgress(R.string.toast_no_google_account_selected);
        // Failure.
        return false;
      }

      if (DEBUG) {
        Log.d(LOG_TAG, "Attempting to get AuthToken for account: " + mEmailAccount);
      }

      try {
        // If the application has the appropriate access then a token will be retrieved, otherwise
        // an error will be thrown.
        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
            MainActivity.this, AppConstants.AUDIENCE);
        credential.setSelectedAccountName(emailAccount);

        String accessToken = credential.getToken();

        if (DEBUG) {
          Log.d(LOG_TAG, "AccessToken retrieved");
        }

        // Success.
        return true;
      } catch (GoogleAuthException unrecoverableException) {
        Log.e(LOG_TAG, "Exception checking OAuth2 authentication.", unrecoverableException);
        publishProgress(R.string.toast_exception_checking_authorization);
        // Failure.
        return false;
      } catch (IOException ioException) {
        Log.e(LOG_TAG, "Exception checking OAuth2 authentication.", ioException);
        publishProgress(R.string.toast_exception_checking_authorization);
        // Failure or cancel request.
        return false;
      }
    }

    @Override
    protected void onProgressUpdate(Integer... stringIds) {
      // Toast only the most recent.
      Integer stringId = stringIds[0];
      Toast.makeText(MainActivity.this, stringId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPreExecute() {
      mAuthTask = this;
    }

    @Override
    protected void onPostExecute(Boolean success) {
      TextView emailAddressTV = (TextView) MainActivity.this.findViewById(R.id.gameID_tv);
      if (success) {
        // Authorization check successful, set internal variable.
        //mEmailAccount = emailAddressTV.getText().toString();
      } else {
        // Authorization check unsuccessful, reset TextView to empty.
        emailAddressTV.setText("");
      }
      mAuthTask = null;
    }

    @Override
    protected void onCancelled() {
      mAuthTask = null;
    }
  }

  /**
   * Simple use of an ArrayAdapter but we're using a static class to ensure no references to the
   * Activity exists.
   */
  static class GreetingsDataAdapter extends ArrayAdapter {
    GreetingsDataAdapter(Application application) {
        super(application.getApplicationContext(), android.R.layout.simple_list_item_1,
                application.greetings);
    }

    void replaceData(HelloGreeting[] greetings) {
      clear();
      for (HelloGreeting greeting : greetings) {
        add(greeting);
      }
    }
      void replaceData_question(HelloQuestionRequest[] greetings) {
          clear();
          for (HelloQuestionRequest greeting : greetings) {
              add(greeting);
          }
      }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView)super.getView(position, convertView, parent);

      HelloGreeting greeting = (HelloGreeting)this.getItem(position);
        //HelloQuestionRequest greeting = (HelloQuestionRequest)this.getItem(position);

      StringBuilder sb = new StringBuilder();

      Set<String> fields = greeting.keySet();
      boolean firstLoop = true;
      for (String fieldName : fields) {
        // Append next line chars to 2.. loop runs.
        if (firstLoop) {
          firstLoop = false;
        } else {
          sb.append("\n");
        }

        sb.append(fieldName)
          .append(": ")
          .append(greeting.get(fieldName));
      }

      view.setText(sb.toString());
      return view;
    }
  }

    public class GetUsernameTask extends AsyncTask<Void, Void, Void> {
        Activity mActivity;
        String mScope;
        String mEmail;

        GetUsernameTask(Activity activity, String name, String scope) {
            this.mActivity = activity;
            this.mScope = scope;
            this.mEmail = name;
        }

        /**
         * Executes the asynchronous job. This runs when you call execute()
         * on the AsyncTask instance.
         */
        @Override
        protected Void doInBackground(Void... p) {
            try {
                String token = fetchToken();
                if (token != null) {
                    // **Insert the good stuff here.**
                    // Use the token to access the user's Google data.
                    Log.v("LOG_TAG", "doInBackground");
                }
            } catch (IOException e) {
                // The fetchToken() method handles Google-specific exceptions,
                // so this indicates something went wrong at a higher level.
                // TIP: Check for network connectivity before starting the AsyncTask.
                Log.v("LOG_TAG", "fetchToken error");
            }
            return null;
        }

        /**
         * Gets an authentication token from Google and handles any
         * GoogleAuthException that may occur.
         */
        protected String fetchToken() throws IOException {
            try {
                return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
            } catch (UserRecoverableAuthException userRecoverableException) {
                // GooglePlayServices.apk is either old, disabled, or not present
                // so we need to show the user some UI in the activity to recover.

                // mActivity.handleException(userRecoverableException);
            } catch (GoogleAuthException fatalException) {
                // Some other type of unrecoverable exception has occurred.
                // Report and log the error as appropriate for your app.

            }
            return null;
        }

    }

    /**
     * This method is invoked when the "List Greetings" button is clicked. See activity_main.xml for
     * the dynamic reference to this method.
     */
    public void onClickListGreetings(View unused) {

        // Use of an anonymous class is done for sample code simplicity. {@code AsyncTasks} should be
        // static-inner or top-level classes to prevent memory leak issues.
        // @see http://goo.gl/fN1fuE @26:00 for an great explanation.
        AsyncTask<Void, Void, HelloGreetingCollection> getAndDisplayGreeting =
                new AsyncTask<Void, Void, HelloGreetingCollection> () {
                    @Override
                    protected HelloGreetingCollection doInBackground(Void... unused) {
                        // Retrieve service handle using null credential since this is an unauthenticated call.
                        Helloworld apiServiceHandle = AppConstants.getApiServiceHandle(null);

                        try {
                            Helloworld.Greetings.ListGreeting getGreetingCommand = apiServiceHandle.greetings().listGreeting();
                            HelloGreetingCollection greeting = getGreetingCommand.execute();
                            return greeting;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(HelloGreetingCollection greeting) {
                        if (greeting!=null && greeting.getItems()!=null) {
                            displayGreetings(greeting.getItems().toArray(new HelloGreeting[] {}));
                        } else {
                            Log.e(LOG_TAG, "No greetings were returned by the API.");
                        }
                    }
                };

        getAndDisplayGreeting.execute((Void)null);
    }

    private class GetCookieTask extends AsyncTask <String, Void, Boolean> {
        protected Boolean doInBackground(String... tokens) {
            try {
                // Don't follow redirects
                http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

                HttpGet http_get = new HttpGet("https://yourapp.appspot.com/_ah/login?continue=http://localhost/&auth=" + tokens[0]);
                HttpResponse response;
                response = http_client.execute(http_get);
                if(response.getStatusLine().getStatusCode() != 302)
                    // Response should be a redirect
                    return false;

                for(Cookie cookie : http_client.getCookieStore().getCookies()) {
                    if(cookie.getName().equals("ACSID"))
                        return true;
                }
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            new AuthenticatedRequestTask().execute("http://yourapp.appspot.com/admin/");
        }
    }
    private class AuthenticatedRequestTask extends AsyncTask  <String, Void, HttpResponse>{
        @Override
        protected HttpResponse doInBackground(String... urls) {
            try {
                HttpGet http_get = new HttpGet(urls[0]);
                return http_client.execute(http_get);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(HttpResponse result) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(result.getEntity().getContent()));
                String first_line = reader.readLine();
                Toast.makeText(getApplicationContext(), first_line, Toast.LENGTH_LONG).show();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
        /*
    private class GetAuthTokenCallback implements AccountManagerCallback {
        public void run(AccountManagerFuture result) {
            Bundle bundle;
            try {
                bundle = (Bundle) result.getResult();
                Intent intent = (Intent)bundle.get(AccountManager.KEY_INTENT);
                if(intent != null) {
                    // User input required
                    startActivity(intent);
                } else {
                    onGetAuthToken(bundle);
                }
            } catch (OperationCanceledException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (AuthenticatorException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        protected void onGetAuthToken(Bundle bundle) {
            String auth_token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            new GetCookieTask().execute(auth_token);
        }
    }*/
    /**
     * This method is invoked when the "Multiply Greeting" button is clicked. See activity_main.xml
     * for the dynamic reference to this method.
     */
    /*
    public void onClickSendGreetings(View view) {
        View rootView = view.getRootView();

        TextView greetingCountInputTV = (TextView)rootView.findViewById(R.id.greeting_count_edit_text);
        if (greetingCountInputTV.getText()==null ||
                Strings.isNullOrEmpty(greetingCountInputTV.getText().toString())) {
            Toast.makeText(this, "Input a Greeting Count", Toast.LENGTH_SHORT).show();
            return;
        };

        String greetingCountString = greetingCountInputTV.getText().toString();
        final int greetingCount = Integer.parseInt(greetingCountString);

        TextView greetingTextInputTV = (TextView)rootView.findViewById(R.id.greeting_text_edit_text);
        if (greetingTextInputTV.getText()==null ||
                Strings.isNullOrEmpty(greetingTextInputTV.getText().toString())) {
            Toast.makeText(this, "Input a Greeting Message", Toast.LENGTH_SHORT).show();
            return;
        };

        final String greetingMessageString = greetingTextInputTV.getText().toString();

        // Use of an anonymous class is done for sample code simplicity. {@code AsyncTasks} should be
        // static-inner or top-level classes to prevent memory leak issues.
        // @see http://goo.gl/fN1fuE @26:00 for an great explanation.
        AsyncTask<Void, Void, HelloGreeting> sendGreetings = new AsyncTask<Void, Void, HelloGreeting> () {
            @Override
            protected HelloGreeting doInBackground(Void... unused) {
                // Retrieve service handle using null credential since this is an unauthenticated call.
                Helloworld apiServiceHandle = AppConstants.getApiServiceHandle(null);

                try {
                    HelloGreeting greeting = new HelloGreeting();
                    greeting.setMessage(greetingMessageString);

                    Helloworld.Greetings.Multiply multiplyGreetingCommand = apiServiceHandle.greetings().multiply(greetingCount,
                            greeting);
                    greeting = multiplyGreetingCommand.execute();
                    return greeting;
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Exception during API call", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(HelloGreeting greeting) {
                if (greeting!=null) {
                    displayGreetings(greeting);
                } else {
                    Log.e(LOG_TAG, "No greetings were returned by the API.");
                }
            }
        };

        sendGreetings.execute((Void)null);
    }
    */

    /**
     * This method is invoked when the "Get Greeting" button is clicked. See activity_main.xml for
     * the dynamic reference to this method.
     */
/*
    public void onClickGetGreeting(View view) {
        View rootView = view.getRootView();
        TextView greetingIdInputTV = (TextView)rootView.findViewById(R.id.greeting_id_edit_text);
        if (greetingIdInputTV.getText()==null ||
                Strings.isNullOrEmpty(greetingIdInputTV.getText().toString())) {
            Toast.makeText(this, "Input a Greeting ID", Toast.LENGTH_SHORT).show();
            return;
        };

        String greetingIdString = greetingIdInputTV.getText().toString();
        int greetingId = Integer.parseInt(greetingIdString);

        // Use of an anonymous class is done for sample code simplicity. {@code AsyncTasks} should be
        // static-inner or top-level classes to prevent memory leak issues.
        // @see http://goo.gl/fN1fuE @26:00 for an great explanation.
        AsyncTask<Integer, Void, HelloGreeting> getAndDisplayGreeting =
                new AsyncTask<Integer, Void, HelloGreeting> () {
                    @Override
                    protected HelloGreeting doInBackground(Integer... integers) {
                        // Retrieve service handle using null credential since this is an unauthenticated call.
                        Helloworld apiServiceHandle = AppConstants.getApiServiceHandle(null);

                        try {
                            Helloworld.Greetings.GetGreeting getGreetingCommand = apiServiceHandle.greetings().getGreeting(integers[0]);
                            Log.v(LOG_TAG, "getGreetingCommand: "+getGreetingCommand);
                            HelloGreeting greeting = getGreetingCommand.execute();
                            return greeting;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(HelloGreeting greeting) {
                        if (greeting!=null) {
                            displayGreetings(greeting);

                        } else {
                            Log.e(LOG_TAG, "No greetings were returned by the API.");
                        }
                    }
                };

        getAndDisplayGreeting.execute(greetingId);
    }
*/


}

