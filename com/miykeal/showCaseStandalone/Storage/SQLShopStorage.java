/*
 * Copyright (C) 2012 Sorklin, KellerKindt <sorklin at gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.miykeal.showCaseStandalone.Storage;

import com.miykeal.showCaseStandalone.ShopInternals.Shop;
import com.miykeal.showCaseStandalone.interfaces.ShopStorage;
import java.io.IOException;

/**
 *
 * @author Sorklin <sorklin at gmail.com>
 */
public class SQLShopStorage implements ShopStorage {

    @Override
    public void saveShop(String sha1, Shop p) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Shop loadShop(String sha1) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveShops(Shop[] p) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Shop[] loadshops() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeShop(String sha1) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeAllShops() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
