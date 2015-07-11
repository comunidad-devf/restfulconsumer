public class Twitter extends RestfulConsumer {

  public Twitter() {
      this.setBaseURI("http://api.twitter.com/1");
  }

  public JSONArray userTimeline(String screen_name) throws JSONException, Exception {
      ArrayList<NameValuePair> options = new ArrayList<NameValuePair>();
      options.add(new BasicNameValuePair("screen_name", screen_name));
      return new JSONArray(this.get("/statuses/user_timeline.json", options));
  }

  public JSONObject showUser(String screen_name) throws JSONException, Exception {
      ArrayList<NameValuePair> options = new ArrayList<NameValuePair>();
      options.add(new BasicNameValuePair("screen_name", screen_name));
      return new JSONObject(this.get("/users/show.json", options));
  }

}
