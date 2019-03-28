package com.khoi.ask.dto;

public class OutputMessage extends Message {

  private String time;

  public OutputMessage(String from, String text, String time) {
    setFrom(from);
    setText(text);
    this.time = time;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }
}
