/**
 * MIT License
 *
 * Copyright (c) 2017 zgqq
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
package mah.ui.layout;

import mah.mode.Mode;
import mah.event.EventHandler;
import mah.ui.key.KeyEvent;

/**
 * Created by zgq on 2017-01-08 11:54
 */
public interface Layout {

    void init();

    void setOnKeyPressed(EventHandler<? extends KeyEvent> keyPressedHandler);

    void setOnKeyReleased(EventHandler<? extends KeyEvent> keyReleasedHandler);

    default void onSetCurrentLayout() {}

    default String getName() {
        throw new UnsupportedOperationException("Unable to get layout name");
    }

    default void setDefaultMode() {
        throw new UnsupportedOperationException("Unable to set default mode");
    }

    default Mode getMode() {
        throw new UnsupportedOperationException("Unable to get mode");
    }

    default void registerMode(Mode mode,ModeListener modeListener) {
        throw new UnsupportedOperationException("Unsupport register mode");
    }
}
