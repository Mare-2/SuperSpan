package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ModernAlertDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    text: String? = null,
    content: (@Composable () -> Unit)? = null,
    icon: ImageVector? = null,
    isDestructive: Boolean = false,
    isAltDestructive: Boolean = false,
    confirmText: String? = null,
    onConfirm: (() -> Unit)? = null,
    altText: String? = null,
    onAlt: (() -> Unit)? = null,
    dismissText: String? = null,
    onDismiss: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (icon != null) {
                    val iconColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(iconColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                }
                
                if (title != null) {
                    Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                }
                
                if (content != null) {
                    content()
                    Spacer(Modifier.height(24.dp))
                } else if (text != null) {
                    Text(
                        text,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                }

                if (confirmText != null && onConfirm != null) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = CircleShape,
                        colors = if (isDestructive) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) else ButtonDefaults.buttonColors()
                    ) {
                        Text(confirmText)
                    }
                }
                
                if (altText != null && onAlt != null) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onAlt,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isAltDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                    ) {
                        Text(altText, color = if (isAltDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                    }
                }
                
                if (dismissText != null && onDismiss != null) {
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                        Text(dismissText, color = Color.Gray)
                    }
                }
            }
        }
    }
}
