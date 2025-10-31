package com.example.petcare.domain.model

enum class speciesEnum { dog, cat }
enum class sexEnum { male, female, unknown }
enum class taskTypeEnum { walk, training, grooming, vaccination, deworming, other }
enum class taskStatusEnum { planned, done, skipped, cancelled }
enum class taskPriorityEnum { low, normal, high }
enum class notificationChannelEnum { tasks, meds, general }