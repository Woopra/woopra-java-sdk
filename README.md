Track customers directly in Java using Woopra's Java SDK

The purpose of this SDK is to allow our customers who have servers running Java to track their users by writing only Java code. Tracking directly in Java will allow you to track your users through the back-end without having to handle http requests manually.

The first step is to setup the tracker SDK. To do so, configure the tracker instance as follows (replace mybusiness.com with your website as registered on Woopra):
``` java
woopra = new WoopraTracker("mybusiness.com");
```
You can also configure the timeout (in milliseconds, defaults to 30000 - equivalent to 30 seconds) after which the event will expire and the visit will be marked as offline:
``` java
// set the timeout to 15seconds:
woopra = new WoopraTracker("mybusiness.com").withIdleTimeout(15000);
```

You can configure secure tracking (over https)
``` java
woopra = new WoopraTracker("mybusiness.com").withSecureTracking(true);
```

You can configure http basic auth (You will need to configure basic auth tracking in your Woopra account first):
``` java
woopra = new WoopraTracker("mybusiness.com").withBasicAuth("User..", "Pass...");
```



To track an event, you should first create an instance of WoopraEvent...
``` java
event = new WoopraEvent("play").
      withProperty("artist", "Dave Brubeck").
      withProperty("song", "Take Five").
      withProperty("genre", "Jazz");

```
... and an instance of WoopraVisitor (the user who triggered the event). You can choose to identify the visitor with his EMAIL, or with a UNIQUE_ID of your choice (in this case, make sure to re-use the same ID for a given visitor accross different visits).
``` java
// a WoopraVisitor identified with his email:
visitor = new WoopraVisitor(WoopraVisitor.EMAIL, "johndoe@mybusiness.com");
// a WoopraVisitor identified with a unique ID:
visitor = new WoopraVisitor(WoopraVisitor.UNIQUE_ID, "MYUNIQUEID");
// In both cases, you can then add visitor properties:
visitor.setProperty("email", "johndoe@mybusiness.com");
//no need to do that if you already instanciated the user with his email
visitor.withProperty("name", "John Doe").withProperty("company", "My Business");
```
And you're ready to start tracking events:
``` java
woopra.track(visitor, event);
```
Or just push the user information (without event tracking) by doing:
``` java
woopra.identify(visitor);
```
