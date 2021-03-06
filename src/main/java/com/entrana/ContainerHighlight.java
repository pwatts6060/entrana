package com.entrana;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

@Singleton
public class ContainerHighlight extends WidgetItemOverlay
{
	private final ItemManager itemManager;
	private final EntranaPlugin plugin;
	private final EntranaConfig config;
	private final ProhibitedItems prohibitedItems;

	@Inject
	private ContainerHighlight(ItemManager itemManager, EntranaPlugin plugin, EntranaConfig config)
	{
		this.itemManager = itemManager;
		this.plugin = plugin;
		this.config = config;
		this.prohibitedItems = new ProhibitedItems(itemManager);
		showOnInventory();
		showOnEquipment();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
	{
		int groupId = WidgetInfo.TO_GROUP(itemWidget.getWidget().getId());
		if (!prohibitedItems.isProhibited(itemId) || (groupId != WidgetID.EQUIPMENT_GROUP_ID
			&& groupId != WidgetID.INVENTORY_GROUP_ID
			&& groupId != WidgetID.DEPOSIT_BOX_GROUP_ID))
		{
			System.out.println(WidgetInfo.TO_GROUP(itemWidget.getWidget().getId()));
			return;
		}
		final Rectangle bounds = itemWidget.getCanvasBounds();
		final BufferedImage outline = itemManager.getItemOutline(itemId, itemWidget.getQuantity(), config.color());
		graphics.drawImage(outline, (int) bounds.getX(), (int) bounds.getY(), null);
	}

	private BufferedImage overlay(BufferedImage image, Color color)
	{
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage overlayed = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = overlayed.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.setComposite(AlphaComposite.SrcAtop);
		g.setColor(color);
		g.fillRect(0, 0, w, h);
		g.dispose();
		return overlayed;
	}
}
