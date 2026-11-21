package jm.edu.utech.helpdesk.model;

//details about the comments a user puts on a ticket
public class Comment {

	 private String author;
	 private String text;
	 private String date;

	 //empty comment
	    public Comment() {}
	    //comment constructor
	    public Comment(String author, String text, String date) {
	        this.author = author;
	        this.text = text;
	        this.date = date;
	    }

	   //getters and setters for comments
		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}
		
		//tostring to format the comment in one line
		 @Override
		    public String toString() {
		        return "[" + date + "] " + author + ": " + text;
		    }

}
