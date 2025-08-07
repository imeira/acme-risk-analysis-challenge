#!/bin/bash

# Cores para formatação
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Verificando se o jq está instalado...${NC}"

# Verifica se o jq já está instalado
if command -v jq &> /dev/null; then
    echo -e "${GREEN}✓ jq já está instalado.${NC}"
    jq --version
    exit 0
fi

echo -e "${YELLOW}jq não encontrado. Iniciando instalação...${NC}"
echo

# Função para instalar no Linux
install_linux() {
    if [ -f /etc/debian_version ]; then
        # Debian/Ubuntu
        echo -e "${YELLOW}Instalando jq no Debian/Ubuntu...${NC}"
        sudo apt-get update && sudo apt-get install -y jq
    elif [ -f /etc/redhat-release ]; then
        # RHEL/CentOS
        echo -e "${YELLOW}Instalando jq no RHEL/CentOS...${NC}"
        sudo yum install -y jq
    elif [ -f /etc/arch-release ]; then
        # Arch Linux
        echo -e "${YELLOW}Instalando jq no Arch Linux...${NC}"
        sudo pacman -S --noconfirm jq
    else
        echo -e "${RED}Distribuição Linux não suportada para instalação automática.${NC}"
        return 1
    fi
}

# Função para instalar no macOS
install_macos() {
    echo -e "${YELLOW}Instalando jq no macOS...${NC}"
    if command -v brew &> /dev/null; then
        brew install jq
    else
        echo -e "${RED}Homebrew não encontrado. Instale o Homebrew primeiro.${NC}"
        return 1
    fi
}

# Função para instalar no Windows (WSL, Git Bash, MSYS2)
install_windows() {
    # Verifica se está no WSL
    if grep -q microsoft /proc/version 2>/dev/null; then
        echo -e "${YELLOW}WSL detectado. Instalando jq...${NC}"
        sudo apt-get update && sudo apt-get install -y jq
        return $?
    fi

    # Verifica se tem Chocolatey
    if command -v choco &> /dev/null; then
        echo -e "${YELLOW}Instalando jq via Chocolatey...${NC}"
        choco install jq -y
        return $?
    fi

    # Verifica se tem Winget
    if command -v winget &> /dev/null; then
        echo -e "${YELLOW}Instalando jq via Winget...${NC}"
        winget install -e --id stedolan.jq --accept-source-agreements --accept-package-agreements
        return $?
    fi

    # Se chegou aqui, nenhum gerenciador de pacotes foi encontrado
    echo -e "${RED}Nenhum gerenciador de pacotes encontrado.${NC}"
    return 1
}

# Detecta o sistema operacional
case "$(uname -s)" in
    Linux*)
        if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
            install_windows
        else
            install_linux
        fi
        ;;
    Darwin*)
        install_macos
        ;;
    CYGWIN*|MINGW32*|MSYS*|MINGW*)
        install_windows
        ;;
    *)
        echo -e "${RED}Sistema operacional não suportado: $(uname -s)${NC}"
        exit 1
        ;;
esac

# Verifica se a instalação foi bem-sucedida
if [ $? -eq 0 ] && command -v jq &> /dev/null; then
    echo -e "${GREEN}✓ jq foi instalado com sucesso!${NC}"
    jq --version
    exit 0
else
    echo -e "\n${RED}❌ Falha ao instalar o jq automaticamente.${NC}"
    echo -e "\n${YELLOW}Por favor, instale o jq manualmente seguindo as instruções para o seu sistema:\n${NC}"
    echo "- Linux (Debian/Ubuntu): sudo apt-get install jq"
    echo "- Linux (RHEL/CentOS): sudo yum install jq"
    echo "- macOS: brew install jq"
    echo "- Windows (Chocolatey): choco install jq"
    echo "- Windows (Winget): winget install -e --id stedolan.jq"
    echo -e "\nOu baixe em: ${YELLOW}https://stedolan.github.io/jq/download/${NC}"
    exit 1
fi
