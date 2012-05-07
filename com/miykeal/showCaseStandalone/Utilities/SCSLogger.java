//Taken from:
/*
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.
 * Multiverse 2 is licensed under the BSD License.                                                         *
 * Copyright (c) 2011, The Multiverse Team All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this 
 * list of conditions and the following disclaimer. Redistributions in binary 
 * form must reproduce the above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or other materials provided 
 * with the distribution. Neither the name of the The Multiverse Team nor the 
 * names of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission. THIS SOFTWARE 
 * IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO 
 * EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY 
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.miykeal.showCaseStandalone.Utilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.*;

/**
* Copyright (C) 2011 Kellerkindt <kellerkindt@miykeal.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

public class SCSLogger {

    private FileHandler fh;
    private Logger log;

    /**
     * Creates a new debug logger.
     *
     * @param logger The name of the logger.
     * @param file   The file to log to.
     */
    public SCSLogger(String logger, String file) {
        this.log = Logger.getLogger(logger);

        try {
            this.fh = new FileHandler(file, true);
            this.log.setUseParentHandlers(false);
            for (Handler handler : this.log.getHandlers()) {
                this.log.removeHandler(handler);
            }
            this.log.addHandler(this.fh);
            this.log.setLevel(Level.ALL);
            this.fh.setFormatter(new LogFormatter());
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Log a message at a certain level
     *
     * @param level
     * @param msg
     */
    public void log(Level level, String msg) {
        this.log.log(level, msg);
    }

    private class LogFormatter extends Formatter {
        private final SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            Throwable ex = record.getThrown();

            builder.append(this.date.format(record.getMillis()));
            builder.append(" [");
            builder.append(record.getLevel().getLocalizedName().toUpperCase());
            builder.append("] ");
            builder.append(record.getMessage());
            builder.append('\n');

            if (ex != null) {
                StringWriter writer = new StringWriter();
                ex.printStackTrace(new PrintWriter(writer));
                builder.append(writer);
            }

            return builder.toString();
        }
    }

    public void close() {
        this.fh.close();
    }
}