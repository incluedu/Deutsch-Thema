/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Patric Hollenstein
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.lustenauer.thema.data;

/**
 * Created by hpj on 27.05.2016.
 */
public class Entry {
    private String stichwort, frage, antwort1, antwort2, antwort3;
    private String stichwortPath, fragePath, antwort1Path, antwort2Path, antwort3Path;


    public Entry(String stichwort, String stichwortPath,
                 String frage, String fragePath,
                 String antwort1, String antwort1Path,
                 String antwort2, String antwort2Path,
                 String antwort3, String antwort3Path) {
        this.stichwort = stichwort;
        this.frage = frage;
        this.antwort1 = antwort1;
        this.antwort2 = antwort2;
        this.antwort3 = antwort3;
        this.stichwortPath = stichwortPath;
        this.fragePath = fragePath;
        this.antwort1Path = antwort1Path;
        this.antwort2Path = antwort2Path;
        this.antwort3Path = antwort3Path;
    }


    public Entry(String stichwort, String stichwortPath,
                 String frage, String fragePath,
                 String antwort1, String antwort1Path,
                 String antwort2, String antwort2Path) {
        this.stichwort = stichwort;
        this.frage = frage;
        this.antwort1 = antwort1;
        this.antwort2 = antwort2;
        this.antwort3 = "";
        this.stichwortPath = stichwortPath;
        this.fragePath = fragePath;
        this.antwort1Path = antwort1Path;
        this.antwort2Path = antwort2Path;
        this.antwort3Path = "";
    }

    public Entry(String stichwort, String stichwortPath,
                 String frage, String fragePath,
                 String antwort1, String antwort1Path) {
        this.stichwort = stichwort;
        this.frage = frage;
        this.antwort1 = antwort1;
        this.antwort2 = "";
        this.antwort3 = "";
        this.stichwortPath = stichwortPath;
        this.fragePath = fragePath;
        this.antwort1Path = antwort1Path;
        this.antwort2Path = "";
        this.antwort3Path = "";
    }


    public String getStichwort() {
        return stichwort;
    }

    public String getFrage() {
        return frage;
    }

    public String getAntwort1() {
        return antwort1;
    }

    public String getAntwort2() {
        return antwort2;
    }

    public String getAntwort3() {
        return antwort3;
    }

    public String getStichwortPath() {
        return stichwortPath;
    }

    public String getFragePath() {
        return fragePath;
    }

    public String getAntwort1Path() {
        return antwort1Path;
    }

    public String getAntwort2Path() {
        return antwort2Path;
    }

    public String getAntwort3Path() {
        return antwort3Path;
    }

    @Override
    public String toString() {
        return getStichwort();
    }

}
