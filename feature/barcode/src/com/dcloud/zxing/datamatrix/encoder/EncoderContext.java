/*
 * Copyright 2006-2007 Jeremias Maerki.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dcloud.zxing.datamatrix.encoder;

import java.nio.charset.Charset;

import com.dcloud.zxing.Dimension;

final class EncoderContext {

  String msg;
  private SymbolShapeHint shape;
  private Dimension minSize;
  private Dimension maxSize;
  StringBuilder codewords;
  int pos;
  int newEncoding;
  SymbolInfo symbolInfo;
  private int skipAtEnd;

  EncoderContext(String msg) {
    //From this point on Strings are not Unicode anymore!
    byte[] msgBinary = msg.getBytes(Charset.forName("ISO-8859-1"));
    StringBuilder sb = new StringBuilder(msgBinary.length);
    for (int i = 0, c = msgBinary.length; i < c; i++) {
      char ch = (char) (msgBinary[i] & 0xff);
      if (ch == '?' && msg.charAt(i) != '?') {
        throw new IllegalArgumentException("Message contains characters outside ISO-8859-1 encoding.");
      }
      sb.append(ch);
    }
    this.msg = sb.toString(); //Not Unicode here!
    shape = SymbolShapeHint.FORCE_NONE;
    this.codewords = new StringBuilder(msg.length());
    newEncoding = -1;
  }

  public void setSymbolShape(SymbolShapeHint shape) {
    this.shape = shape;
  }

  public void setSizeConstraints(Dimension minSize, Dimension maxSize) {
    this.minSize = minSize;
    this.maxSize = maxSize;
  }

  public String getMessage() {
    return this.msg;
  }

  public void setSkipAtEnd(int count) {
    this.skipAtEnd = count;
  }

  public char getCurrentChar() {
    return msg.charAt(pos);
  }

  public char getCurrent() {
    return msg.charAt(pos);
  }

  public void writeCodewords(String codewords) {
    this.codewords.append(codewords);
  }

  public void writeCodeword(char codeword) {
    this.codewords.append(codeword);
  }

  public int getCodewordCount() {
    return this.codewords.length();
  }

  public void signalEncoderChange(int encoding) {
    this.newEncoding = encoding;
  }

  public void resetEncoderSignal() {
    this.newEncoding = -1;
  }

  public boolean hasMoreCharacters() {
    return pos < getTotalMessageCharCount();
  }

  private int getTotalMessageCharCount() {
    return msg.length() - skipAtEnd;
  }

  public int getRemainingCharacters() {
    return getTotalMessageCharCount() - pos;
  }

  public void updateSymbolInfo() {
    updateSymbolInfo(getCodewordCount());
  }

  public void updateSymbolInfo(int len) {
    if (this.symbolInfo == null || len > this.symbolInfo.dataCapacity) {
      this.symbolInfo = SymbolInfo.lookup(len, shape, minSize, maxSize, true);
    }
  }

  public void resetSymbolInfo() {
    this.symbolInfo = null;
  }
}
