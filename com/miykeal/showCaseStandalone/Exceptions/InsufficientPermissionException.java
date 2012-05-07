package com.miykeal.showCaseStandalone.Exceptions;

import com.miykeal.showCaseStandalone.Utilities.Localization;

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

public class InsufficientPermissionException extends Exception {
	private final static long serialVersionUID = 1l;
	private String message;
	
	/*
	 * Setting the message of the Exception to a new String
	 */
	public InsufficientPermissionException () {
		this.message = Localization.get("insufficientPermissionException");
//		this.message = "You do not have permission for that.";//msg: insufficientPermissionException
	}
	
	/*
	 * Setting the message of the Exception to the given message
	 */
	public InsufficientPermissionException (String message) {
		this.message = message;
	}
	
	/*
	 * returns the message
	 */
	@Override
	public String getMessage () {
		return this.message;
	}
}
