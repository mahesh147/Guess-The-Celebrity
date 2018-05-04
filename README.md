# Guess-The-Celebrity

A simple android app that allows the user to guess the name of the celebrity based on their picture.

## Note

The app takes a very long time to load at the start, because fetching the HTML content and pictures of various celebrities takes place in a single thread, i.e the main thread. None of the retrieval process is made to run in the background. Changing this will make the app load much faster in the start.
