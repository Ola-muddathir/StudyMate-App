package com.example.studymate.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun HomeScreen(viewModel: DashboardViewModel) {

    val dashboardState by viewModel.dashboardState.collectAsState()
    Scaffold { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Text(
                    text = "StudyMate",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Welcome back! 👋",
                    fontSize = 20.sp
                )

                Text(
                    text = "Stay organized and achieve your goals."
                )
            }

            item {
                Text(
                    text = "Your Statistics",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardCard(
                        "Total Tasks",
                        dashboardState.totalTasks.toString(),
                        Modifier.weight(1f)
                    )

                    DashboardCard(
                        "Completed",
                        dashboardState.completedTasks.toString(),
                        Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardCard(
                        "Upcoming",
                        dashboardState.upcomingTasks.toString(),
                        Modifier.weight(1f)
                    )

                    DashboardCard(
                        "Overdue",
                        dashboardState.overdueTasks.toString(),
                        Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = "Upcoming Tasks",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Your upcoming assignments will appear here.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    count: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(text = title)
        }
    }
}
