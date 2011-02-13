/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2011 dimitry
 *
 *  This file author is dimitry
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.features.mindmapmode.encrypt;

import org.freeplane.core.controller.Controller;
import org.freeplane.core.ui.MenuBuilder;
import org.freeplane.core.undo.IActor;
import org.freeplane.features.common.encrypt.EncryptionController;
import org.freeplane.features.common.map.EncryptionModel;
import org.freeplane.features.common.map.ModeController;
import org.freeplane.features.common.map.NodeModel;

/**
 * @author Dimitry Polivaev
 * Feb 13, 2011
 */
public class MEncryptionController extends EncryptionController {
	public static void install(MEncryptionController encryptionController){
		EncryptionController.install(encryptionController);
		final ModeController modeController = Controller.getCurrentModeController();
		final MenuBuilder menuBuilder = modeController.getUserInputListenerFactory().getMenuBuilder();
		final RemoveEncryption removeEncryptionAction = new RemoveEncryption(encryptionController);
		modeController.addAction(removeEncryptionAction);
		menuBuilder.addAnnotatedAction(removeEncryptionAction);
		final EncryptedMap encryptedMapAction = new EncryptedMap();
		menuBuilder.addAnnotatedAction(encryptedMapAction);
		modeController.addAction(encryptedMapAction);
	}
	
	public void removeEncryption(final NodeModel node) {
		final EncryptionModel encryptedMindMapNode = EncryptionModel.getModel(node);
		if (encryptedMindMapNode == null) {
			return;
		}
		if(! encryptedMindMapNode.isAccessible())
			toggleCryptState(node);
		if(! encryptedMindMapNode.isAccessible())
			return;
		final IActor actor = new IActor() {
			public void act() {
				node.removeExtension(encryptedMindMapNode);
				node.removeStateIcons("decrypted");
				Controller.getCurrentModeController().getMapController().nodeChanged(node);
			}

			public String getDescription() {
				return "removeEncryption";
			}

			public void undo() {
				node.addExtension(encryptedMindMapNode);
				encryptedMindMapNode.updateIcon();
				Controller.getCurrentModeController().getMapController().nodeChanged(node);
			}
		};
		Controller.getCurrentModeController().execute(actor, node.getMap());
		
    }

}
