#!/usr/bin/env bash

# ANSI Color Codes for Conference Projector Readability
CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BOLD='\033[1m'
NC='\033[0m' # No Color

clear
echo -e "${CYAN}${BOLD}====================================================================${NC}"
echo -e "${GREEN}${BOLD}   JUGAAD VS JFR — LIVE DEMO SUITE                                  ${NC}"
echo -e "${YELLOW}   Ashish Vaghela | Software Crafter @ Nelkinda Software Craft      ${NC}"
echo -e "${CYAN}${BOLD}====================================================================${NC}"
echo -e " ${BOLD}[1]${NC} Run Jackson Reflection Benchmark (Speed of Light Joke)"
echo -e " ${BOLD}[2]${NC} Run Hibernate N+1 Simulation (Dhaba Roti Analogy)"
echo -e " ${BOLD}[3]${NC} Profile Application with Live JFR Recording"
echo -e " ${BOLD}[4]${NC} View JFR Recording Cheatsheet"
echo -e " ${BOLD}[Q]${NC} Exit"
echo -e "${CYAN}${BOLD}====================================================================${NC}"
read -p " Select Option (1-4 or Q): " choice

case $choice in
  1)
    echo -e "\n${GREEN}>> Executing Jackson Reflection Benchmark...${NC}\n"
    java 01-jackson-reflection/ReflectionBenchFun.java
    ;;
  2)
    echo -e "\n${GREEN}>> Executing Hibernate N+1 Query Simulation...${NC}\n"
    java 02-hibernate-n-plus-one/NPlusOneSimFun.java
    ;;
  3)
    echo -e "\n${GREEN}>> Starting App with Java Flight Recorder (-XX:StartFlightRecorder)...${NC}\n"
    java -XX:StartFlightRecorder=filename=app.jfr,settings=profile 03-jfr-flight-deck/AppWithJFR.java
    echo -e "\n${YELLOW}Saved profile to ${BOLD}app.jfr${NC}${YELLOW}! Open in JDK Mission Control or async-profiler.${NC}\n"
    ;;
  4)
    echo -e "\n${CYAN}${BOLD}JFR CLI Commands:${NC}"
    echo -e " 1. Start: ${YELLOW}jcmd <PID> JFR.start settings=profile filename=recording.jfr${NC}"
    echo -e " 2. Dump:  ${YELLOW}jcmd <PID> JFR.dump filename=output.jfr${NC}"
    echo -e " 3. Stop:  ${YELLOW}jcmd <PID> JFR.stop${NC}\n"
    ;;
  [qQ])
    echo -e "\n${YELLOW}Happy crafting! See you on stage.${NC}\n"
    exit 0
    ;;
  *)
    echo -e "\n${RED}Invalid selection.${NC}\n"
    ;;
esac
